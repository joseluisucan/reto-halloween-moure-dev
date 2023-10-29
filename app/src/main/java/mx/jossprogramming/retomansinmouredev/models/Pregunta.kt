package mx.jossprogramming.retomansinmouredev.models

import com.google.gson.annotations.SerializedName

class Pregunta() {
     val pregunta: String = ""
     val opciones: List<String> = emptyList()
     @SerializedName("respuesta_correcta")
     val respuestaCorrecta: String = ""
 }