package mx.jossprogramming.retomansinmouredev.models

import com.airbnb.lottie.utils.Utils
import mx.jossprogramming.retomansinmouredev.R

sealed class Personaje {
    open var icono:Int = 0
    open var visible = true

    data class Persona(
        override var icono: Int = R.drawable.baseline_android_24
    ):Personaje()

    class Inicial:Personaje()

    class Puerta:Personaje(){
        override var icono: Int = R.drawable.baseline_door_sliding_24
        override var visible: Boolean = true
    }

    data class Enigma(
        override var icono: Int = R.drawable.baseline_question_mark_24,
        override var visible: Boolean = true,
        var pregunta:Pregunta
    ):Personaje()

    class Fantasma:Personaje(){
        override var icono: Int = R.drawable.baseline_person_3_24
        override var visible: Boolean = true
    }

    class Dulce:Personaje(){
        override var icono: Int = R.drawable.baseline_cake_24
        override var visible: Boolean = true
    }
}