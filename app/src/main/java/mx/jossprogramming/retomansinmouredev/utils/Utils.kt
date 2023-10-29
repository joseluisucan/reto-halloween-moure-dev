package mx.jossprogramming.retomansinmouredev.utils

import android.content.Context
import com.google.gson.Gson
import mx.jossprogramming.retomansinmouredev.R
import mx.jossprogramming.retomansinmouredev.models.Pregunta
import java.io.InputStreamReader

object Utils {
    fun getRandomQuestion(context: Context): Pregunta {
        val jsonId = R.raw.questions
        val inputStream = context.resources.openRawResource(jsonId)
        val json = InputStreamReader(inputStream).readText()
        val gson = Gson()
        val preguntasArray: Array<Pregunta> = gson.fromJson(json, Array<Pregunta>::class.java)
        return preguntasArray.random()
    }
}