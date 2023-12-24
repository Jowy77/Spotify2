package com.example.spotify2.viewModel

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.AnyRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.spotify2.R
import com.example.spotify2.clases.Cancion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class ExoPlayerViewModel : ViewModel() {

    private val _exoPlayer: MutableStateFlow<ExoPlayer?> = MutableStateFlow(null)
    val exoPlayer = _exoPlayer.asStateFlow()

    private val _duracion = MutableStateFlow(0)
    val duracion = _duracion.asStateFlow()

    private val _progreso = MutableStateFlow(0)
    val progreso = _progreso.asStateFlow()

    private val cancionesList = listOf(
        Cancion(
            "Time - PinkFloyd",
            "Dark Side Of The Moon",
            R.drawable.pinkfloyd,
            R.raw.time),

        Cancion("Hold The Line - Toto",
            "Toto 1978",
            R.drawable.toto,
            R.raw.holdtheline),
        Cancion(
            "Killer Queen - Queen",
            "Sheer Heart Attack",
            R.drawable.killerqueen,
            R.raw.killerqueen),

        Cancion(
            "Vienna - Billy Joel",
            "The Stranger",
            R.drawable.vienna,
            R.raw.vienna),

        Cancion(
            "Whole Lotta Rosie",
            "Let There Be Rock",
            R.drawable.wholelottarosie,
            R.raw.wholelottarosie)
    )

    private val _cancionActual = MutableStateFlow(cancionesList[0])
    val cancionActual = _cancionActual.asStateFlow()

    private var indiceActual = 0

    private val _repetirCancion = MutableStateFlow(false)
    val repetirCancion = _repetirCancion.asStateFlow()
    private val _cancionRandom = MutableStateFlow(false)
    val cancionRandom = _cancionRandom.asStateFlow()

    fun ExoPlayer(context: Context) {

        _exoPlayer.value = ExoPlayer.Builder(context).build()
        _exoPlayer.value!!.prepare()
        _exoPlayer.value!!.playWhenReady = true

    }

    fun play(context: Context) {
        val mediaItem = MediaItem.fromUri(obtenerRuta(context, _cancionActual.value.cancion))
        setExoPlayer(mediaItem)
    }

    private fun setExoPlayer(mediaItem: MediaItem) {
        val exoPlayer = _exoPlayer.value ?: return

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = true

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                reproduccionStatusOnChange(playbackState)
            }
        })
    }

    private fun reproduccionStatusOnChange(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _duracion.value = _exoPlayer.value?.duration?.toInt() ?: 0

                viewModelScope.launch {
                    while (isActive) {
                        _progreso.value = _exoPlayer.value?.currentPosition?.toInt() ?: 0
                        delay(1000)
                    }
                }
            }
            // Agrega casos para otros estados si es necesario
            else -> {
                // Manejar otros estados si es necesario
            }
        }
    }

    override fun onCleared() {
        _exoPlayer.value!!.release()
        super.onCleared()
    }

    fun onMusic() {
        if (_exoPlayer.value!!.isPlaying) {
            _exoPlayer.value!!.pause()
        } else {
            _exoPlayer.value!!.play()
        }
    }

    fun changeSong(context: Context) {
        stopCleanExoPlayer()

        if (repetirCancion.value) {
            playCurrentSong(context)
        } else {
            nextSong(context)
        }
    }

    private fun stopCleanExoPlayer() {
        _exoPlayer.value?.stop()
        _exoPlayer.value?.clearMediaItems()
    }

    private fun playCurrentSong(context: Context) {
        _exoPlayer.value?.setMediaItem(MediaItem.fromUri(obtenerRuta(context, _cancionActual.value.cancion)))
        _exoPlayer.value?.prepare()
        _exoPlayer.value?.playWhenReady = true
    }

    private fun nextSong(context: Context) {
        if (_cancionRandom.value) {
            randomSong(context)
        } else {
            nextSongList(context)
        }
    }

    private fun randomSong(context: Context) {
        var cancionAleatoria: Int

        do {
            cancionAleatoria = (cancionesList.indices).random()
        } while (cancionAleatoria == indiceActual)

        indiceActual = cancionAleatoria
        actualizarCancionYReproducir(context)
    }

    private fun nextSongList(context: Context) {
        indiceActual = if (indiceActual < cancionesList.size - 1) {
            indiceActual + 1
        } else {
            0
        }
        actualizarCancionYReproducir(context)
    }

    private fun actualizarCancionYReproducir(context: Context) {
        _cancionActual.value = cancionesList[indiceActual]
        playCurrentSong(context)
    }

    fun SliderMovement(posisionSlider: Int) {
        _exoPlayer.value?.seekTo(posisionSlider.toLong())
    }

    fun checkLoopButton() {
        _repetirCancion.value = !_repetirCancion.value
    }

    fun chackRandomButton() {
        _cancionRandom.value = !_cancionRandom.value
    }

    fun previousSong(context: Context) {
        stopCleanExoPlayer()

        indiceActual = if (indiceActual > 0) {
            indiceActual - 1
        } else {
            cancionesList.size - 1
        }
        actualizarCancionYReproducir(context)
    }

    @Throws(Resources.NotFoundException::class)
    fun obtenerRuta(context: Context, @AnyRes resId: Int): Uri {
        val res: Resources = context.resources
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId)
                    + '/' + res.getResourceTypeName(resId)
                    + '/' + res.getResourceEntryName(resId)
        )
    }
}