package com.example.spotify2.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spotify2.pantallas.ReproductorView

@Composable
fun GrafoDeNavegacion() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.ReproductorView.ruta) {
        composable(Rutas.ReproductorView.ruta) {

            ReproductorView(navController =  navController)
        }
    }

}