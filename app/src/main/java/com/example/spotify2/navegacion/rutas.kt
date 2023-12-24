package com.example.spotify2.navegacion

sealed class Rutas (val ruta : String) {
    object ReproductorView: Rutas("reproductorview")
}