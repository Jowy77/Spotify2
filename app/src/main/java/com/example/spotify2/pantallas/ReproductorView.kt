package com.example.spotify2.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spotify2.viewModel.ExoPlayerViewModel

fun CrearValorSlider(tiempo: Int): String {

    val segundos = tiempo / 1000
    val minutos = segundos / 60
    val tiempoRestante = segundos % 60
    val formateoTiempoCancion = String.format("%02d:%02d", minutos, tiempoRestante)

    return formateoTiempoCancion;
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ReproductorView(navController: NavController?) {

    val contexto = LocalContext.current
    //----------------------------------------------------------------------------------------------------------
    val exoPlayerViewModel: ExoPlayerViewModel = viewModel()
    val progreso by exoPlayerViewModel.progreso.collectAsState()
    val duracion by exoPlayerViewModel.duracion.collectAsState()
    //----------------------------------------------------------------------------------------------------------
    LaunchedEffect(Unit){
        exoPlayerViewModel.ExoPlayer(contexto)
        exoPlayerViewModel.play(contexto)
    }
    //----------------------------------------------------------------------------------------------------------
    var iconRandom by remember { mutableStateOf(androidx.media3.ui.R.drawable.exo_styled_controls_shuffle_off) }
    var iconoRepetir by remember { mutableStateOf(androidx.media3.ui.R.drawable.exo_styled_controls_repeat_off) }
    var iconoPlay by remember { mutableStateOf(androidx.media3.ui.R.drawable.exo_styled_controls_pause) }
    //----------------------------------------------------------------------------------------------------------
    var pausado by remember { mutableStateOf(false) }


    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.background(color = Color.Magenta)){
        //-------------------------------------------TITULO ALBUM----------------------------------------------------------
        Text(text = exoPlayerViewModel.cancionActual.value.album, modifier = Modifier
            .weight(0.2f).align(Alignment.CenterHorizontally).padding(8.dp), fontSize = 25.sp)
        //-------------------------------------------ALBUM PORTADA---------------------------------------------------------
        Image(painter = painterResource(id = exoPlayerViewModel.cancionActual.value.cover), contentDescription = "",
            modifier = Modifier.fillMaxWidth().padding(8.dp).size(250.dp).weight(1f))
        //-------------------------------------------TUTULO CANCION--------------------------------------------------------
        Text(text = exoPlayerViewModel.cancionActual.value.nombre, modifier = Modifier
            .align(Alignment.CenterHorizontally).padding(8.dp).weight(0.3f), fontSize = 25.sp)
        //-------------------------------------------SLIDER PROGRESO CANCION--------------------------------------------------------
        Slider(value = progreso.toFloat(),
            onValueChange = {Posicion -> exoPlayerViewModel.SliderMovement(Posicion.toInt())},
            steps = 200, valueRange = 0f..duracion.toFloat(),
            modifier = Modifier.fillMaxWidth().padding(10.dp).weight(0.1f))
        //-------------------------------------------VALORES SLIDER-------------------------------------------------------
        Row (horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(15.dp, 0.dp, 15.dp, 15.dp).weight(0.1f)){
            Text(text = CrearValorSlider(progreso),fontSize = 20.sp)
            Text(text = CrearValorSlider(duracion),fontSize = 20.sp)
        }
        //-------------------------------------------BOTONES-------------------------------------------------------
        Row (horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(8.dp, 8.dp, 8.dp, 8.dp).weight(0.3f)) {
            //ICONO RANDOM---------------------------------------------------------------------
            IconButton(
                onClick = {
                    exoPlayerViewModel.chackRandomButton()
                    if (exoPlayerViewModel.cancionRandom.value) {
                        iconRandom = androidx.media3.ui.R.drawable.exo_styled_controls_shuffle_on
                    }
                    else {
                        iconRandom = androidx.media3.ui.R.drawable.exo_styled_controls_shuffle_off
                    }},
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0f))
            ) {
                Icon(
                    painter = painterResource(id = iconRandom),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface)
            }
            //ICONO RETROCEDER---------------------------------------------------------------------
            IconButton(onClick = { exoPlayerViewModel.previousSong(contexto) },
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0f))) {

                Icon(
                    painter = painterResource(id = androidx.media3.ui.R.drawable.exo_styled_controls_previous),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
            //ICONO PLAY/PAUSE---------------------------------------------------------------------
            IconButton(onClick = {
                exoPlayerViewModel.onMusic()
                pausado = !pausado
                if (pausado) {
                    iconoPlay = androidx.media3.ui.R.drawable.exo_styled_controls_play
                }
                else {
                    iconoPlay = androidx.media3.ui.R.drawable.exo_styled_controls_pause
                } },
                modifier = Modifier.size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0f))) {

                Icon(
                    painter = painterResource(iconoPlay),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(800.dp)
                )
            }
            //ICONO AVANZAR---------------------------------------------------------------------
            IconButton(onClick = { exoPlayerViewModel.changeSong(contexto) },
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0f))) {

                Icon(
                    painter = painterResource(id = androidx.media3.ui.R.drawable.exo_styled_controls_next),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
            //ICONO BUCLE---------------------------------------------------------------------
            IconButton(onClick = {
                exoPlayerViewModel.checkLoopButton()
                if (exoPlayerViewModel.repetirCancion.value) {
                    iconoRepetir = androidx.media3.ui.R.drawable.exo_styled_controls_repeat_one
                }
                else {
                    iconoRepetir = androidx.media3.ui.R.drawable.exo_styled_controls_repeat_off
                } },
                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0f))) {

                Icon(
                    painter = painterResource(id = iconoRepetir),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    }
}