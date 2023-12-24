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

    private val listaDeCanciones = listOf(
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

    private val _cancionActual = MutableStateFlow(listaDeCanciones[0])
    val cancionActual = _cancionActual.asStateFlow()

    private val _duracion = MutableStateFlow(0)
    val duracion = _duracion.asStateFlow()

    private val _progreso = MutableStateFlow(0)
    val progreso = _progreso.asStateFlow()



    private var indiceCancionActual = 0

    private val _repetirCancion = MutableStateFlow(false)
    val repetirCancion = _repetirCancion.asStateFlow()
    private val _cancionRandom = MutableStateFlow(false)
    val cancionRandom = _cancionRandom.asStateFlow()

    fun crearExoPlayer(context: Context) {

        _exoPlayer.value = ExoPlayer.Builder(context).build()
        _exoPlayer.value!!.prepare()
        _exoPlayer.value!!.playWhenReady = true

    }

    fun empezarMusica(context: Context) {

        var mediaItem = MediaItem.fromUri(obtenerRuta(context, _cancionActual.value.cancion))
        _exoPlayer.value!!.setMediaItem(mediaItem)
        _exoPlayer.value!!.playWhenReady = true
        _exoPlayer.value!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _duracion.value = _exoPlayer.value!!.duration.toInt()

                    viewModelScope.launch {
                        while (isActive) {
                            _progreso.value = _exoPlayer.value!!.currentPosition.toInt()
                            delay(1000)
                        }
                    }
                } else if (playbackState == Player.STATE_BUFFERING) {
                    // El Player está cargando el archivo, preparando la reproducción.
                    // No está listo, pero está en ello.
                } else if (playbackState == Player.STATE_ENDED) {
                    // El Player ha terminado de reproducir el archivo.
                    //CambiarCancion(context)

                } else if (playbackState == Player.STATE_IDLE) {
                    // El player se ha creado, pero no se ha lanzado la operación prepared.
                }

            }
        }
        )
    }

    override fun onCleared() {
        _exoPlayer.value!!.release()
        super.onCleared()
    }

    fun pausarReanudarMusica() {
        if (_exoPlayer.value!!.isPlaying) {
            _exoPlayer.value!!.pause()
        } else {
            _exoPlayer.value!!.play()
        }
    }

    fun cambiarCancion(context: Context) {
        _exoPlayer.value?.stop()
        _exoPlayer.value?.clearMediaItems()

        if (repetirCancion.value) {
            _exoPlayer.value?.setMediaItem(MediaItem.fromUri(obtenerRuta(context, _cancionActual.value.cancion)))
            _exoPlayer.value?.prepare()
            _exoPlayer.value?.playWhenReady = true
        } else {
            if (_cancionRandom.value) {
                var cancionAleatoria: Int

                do {
                    cancionAleatoria = (listaDeCanciones.indices).random() + indiceCancionActual
                } while (cancionAleatoria >= listaDeCanciones.size || cancionAleatoria == indiceCancionActual)
                indiceCancionActual = cancionAleatoria
            } else {
                indiceCancionActual++
            }
            if (!repetirCancion.value && indiceCancionActual == listaDeCanciones.size) {
                indiceCancionActual = 0
            }
            _cancionActual.value = listaDeCanciones[indiceCancionActual]

            _exoPlayer.value?.setMediaItem(MediaItem.fromUri(obtenerRuta(context, _cancionActual.value.cancion)))
            _exoPlayer.value?.prepare()
            _exoPlayer.value?.playWhenReady = true
        }
    }
    fun movimientoSlider(siguienteSector: Int) {
        _exoPlayer.value?.seekTo(siguienteSector.toLong())
    }

    fun pulsadoBotonRepetir() {
        _repetirCancion.value = !_repetirCancion.value
    }

    fun pulsadoBotonRandom() {
        _cancionRandom.value = !_cancionRandom.value
    }

    fun retrocederCancion(context: Context) {
        _exoPlayer.value!!.stop()
        _exoPlayer.value!!.clearMediaItems()

        indiceCancionActual = if (indiceCancionActual > 0) {
            indiceCancionActual - 1
        } else {
            listaDeCanciones.size - 1
        }
        _cancionActual.value = listaDeCanciones[indiceCancionActual]

        _exoPlayer.value!!.setMediaItem(MediaItem.fromUri(obtenerRuta(context, _cancionActual.value.cancion)))
        _exoPlayer.value!!.prepare()
        _exoPlayer.value!!.playWhenReady = true
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