package mx.jossprogramming.retomansinmouredev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import mx.jossprogramming.retomansinmouredev.models.Personaje
import mx.jossprogramming.retomansinmouredev.models.Pregunta
import mx.jossprogramming.retomansinmouredev.ui.theme.RetoMansiónMoureDevTheme
import mx.jossprogramming.retomansinmouredev.utils.Utils

class MainActivity : ComponentActivity() {
    val sizeIcon = 60.dp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val contexto = LocalContext.current
            var matriz by remember { mutableStateOf(Array(4) { Array<Personaje>(4) { Personaje.Inicial() } }) }
            val maximoFantasmas = 1
            var posYPuerta = (0..3).random()
            var posYDulce = (0..3).random()
            matriz[0][posYPuerta] = Personaje.Puerta()
            matriz[3][posYDulce] = Personaje.Dulce()

            var posXPersonaje by remember { mutableStateOf(0) }
            var posYPersonaje by remember { mutableStateOf(posYPuerta) }

            var mostrarPregunta by remember { mutableStateOf(false) }
            var mostrarPreguntaFantasma by remember{ mutableStateOf(false) }
            var mostrarExito by remember{ mutableStateOf(false) }
            var preguntaActual by remember { mutableStateOf(Pregunta()) }
            var contadorPreguntasFantasmas = 0

            for (i in 0..maximoFantasmas) {
                matriz = colocarFantasma(matriz)
            }

            colocarEnigmas(matriz, contexto)

            RetoMansiónMoureDevTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        if (mostrarPregunta) {
                            ComposablePregunta(
                                p = preguntaActual,
                                onPreguntaSuccess = {
                                    mostrarPregunta = false
                                                    },
                                onPreguntaFail = {
                                    Toast.makeText(
                                        contexto,
                                        "Error. Intenta de nuevo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                        else if(mostrarPreguntaFantasma ){
                            ComposablePreguntaFantasma(
                                p = preguntaActual,
                                onPreguntaSuccess = {
                                    mostrarPreguntaFantasma = false
                                },
                                onPreguntaFail = {
                                    Toast.makeText(
                                        contexto,
                                        "Error. Intenta de nuevo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                        else if(mostrarExito){
                            ComposableExito {
                                mostrarExito = false
                                val intent = Intent(this@MainActivity,MainActivity::class.java)
                                startActivity(intent)
                                this@MainActivity.finish()
                            }
                        }
                        else{
                            Column(modifier = Modifier.fillMaxSize().background(color = Color.White)) {

                                matriz.forEachIndexed { i, personajesArray ->
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        personajesArray.forEachIndexed { j, p ->
                                            val isPersonajeHere = (i == posXPersonaje && j == posYPersonaje)
                                            ComposablePersonaje(
                                                p = p,
                                                isPersonajeHere = isPersonajeHere
                                            )

                                            LaunchedEffect(key1 = isPersonajeHere, key2 = p is Personaje.Enigma){
                                                if (isPersonajeHere && p is Personaje.Enigma) {
                                                    matriz[i][j] = Personaje.Inicial()
                                                    preguntaActual  = Utils.getRandomQuestion(contexto)
                                                    mostrarPregunta = true
                                                }
                                            }
                                            LaunchedEffect(key1 = isPersonajeHere, key2 = p is Personaje.Fantasma ){
                                                if (isPersonajeHere && p is Personaje.Fantasma) {
                                                    if(contadorPreguntasFantasmas < 1){
                                                        matriz[i][j] = Personaje.Fantasma()
                                                        contadorPreguntasFantasmas += 1
                                                    }else{
                                                        matriz[i][j] = Personaje.Inicial()
                                                        contadorPreguntasFantasmas = 0
                                                    }
                                                    preguntaActual  = Utils.getRandomQuestion(contexto)
                                                    mostrarPreguntaFantasma = true
                                                }
                                            }
                                            LaunchedEffect(key1 = isPersonajeHere, key2 = p is Personaje.Dulce){
                                                if (isPersonajeHere && p is Personaje.Dulce) {
                                                    matriz[i][j] = Personaje.Inicial()
                                                    mostrarExito = true
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Row() {
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .border(1.dp, Color.Black)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (posYPersonaje > 0) {
                                                    posYPersonaje -= 1
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowLeft,
                                                contentDescription = "Left",
                                                Modifier.size(sizeIcon),
                                                tint = Color.Blue
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .border(1.dp, Color.Black)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (posXPersonaje > 0) {
                                                    posXPersonaje -= 1
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowUp,
                                                contentDescription = "Up",
                                                Modifier.size(sizeIcon),
                                                tint = Color.Blue
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .border(1.dp, Color.Black)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (posYPersonaje < 3) {
                                                    posYPersonaje += 1
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "Right",
                                                Modifier.size(sizeIcon),
                                                tint = Color.Blue
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .border(1.dp, Color.Black)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (posXPersonaje < 3) {
                                                    posXPersonaje += 1
                                                }
                                            },
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Down",
                                                Modifier.size(sizeIcon),
                                                tint = Color.Blue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ComposablePersonaje(p: Personaje, isPersonajeHere: Boolean) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(1.dp, Color.Black)
        ) {
            when (p) {
                is Personaje.Puerta -> {
                    Icon(
                        painter = painterResource(id = p.icono),
                        contentDescription = "puerta",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizeIcon),
                        tint = Color.DarkGray
                    )

                    if (isPersonajeHere) {
                        Icon(
                            painter = painterResource(id = Personaje.Persona().icono),
                            contentDescription = "personaje",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(32.dp),
                            tint = Color.Green
                        )
                    }
                }

                is Personaje.Dulce -> {
                    Icon(
                        painter = painterResource(id = p.icono),
                        contentDescription = "dulce",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizeIcon),
                        tint = Color.Magenta
                    )
                    if (isPersonajeHere) {
                        Icon(
                            painter = painterResource(id = Personaje.Persona().icono),
                            contentDescription = "personaje",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(32.dp),
                            tint = Color.Green
                        )
                    }
                }

                is Personaje.Enigma -> {
                    Icon(
                        painter = painterResource(id = p.icono),
                        contentDescription = "enigma",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizeIcon),
                        tint = Color.Red
                    )
                    if (isPersonajeHere) {
                        Icon(
                            painter = painterResource(id = Personaje.Persona().icono),
                            contentDescription = "personaje",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(32.dp),
                            tint = Color.Green
                        )
                    }
                }

                is Personaje.Fantasma -> {
                    Icon(
                        painter = painterResource(id = p.icono),
                        contentDescription = "fantasma",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizeIcon),
                        tint = Color.Gray
                    )
                    if (isPersonajeHere) {
                        Icon(
                            painter = painterResource(id = Personaje.Persona().icono),
                            contentDescription = "personaje",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(32.dp),
                            tint = Color.Green
                        )
                    }
                }

                is Personaje.Inicial -> {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "persona",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizeIcon),
                        tint = Color.Green
                    )
                    if (isPersonajeHere) {
                        Icon(
                            painter = painterResource(id = Personaje.Persona().icono),
                            contentDescription = "personaje",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(32.dp),
                            tint = Color.Green
                        )
                    }
                }
                is Personaje.Persona -> {
                    Icon(
                        painter = painterResource(id = p.icono),
                        contentDescription = "persona",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(sizeIcon),
                        tint = Color.Green
                    )
                    if (isPersonajeHere) {
                        Icon(
                            painter = painterResource(id = Personaje.Persona().icono),
                            contentDescription = "personaje",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(32.dp),
                            tint = Color.Green
                        )
                    }
                }
            }
        }
    }

    fun colocarFantasma(matriz: Array<Array<Personaje>>): Array<Array<Personaje>> {
        var posXFantasma = (0..3).random()
        var posYFantasma = (0..3).random()

        var colocado = false
        while (!colocado) {
            if (matriz[posXFantasma][posYFantasma] is Personaje.Inicial) {
                matriz[posXFantasma][posYFantasma] = Personaje.Fantasma()
                colocado = true
            }
            posXFantasma = (0..3).random()
            posYFantasma = (0..3).random()
        }
        return matriz
    }

    fun colocarEnigmas(matriz: Array<Array<Personaje>>, context: Context): Array<Array<Personaje>> {
        matriz.forEachIndexed { i, personajes ->
            personajes.forEachIndexed { j, p ->
                if (matriz[i][j] is Personaje.Inicial) {
                    matriz[i][j] = Personaje.Enigma(
                        pregunta = Utils.getRandomQuestion(context)
                    )
                }
            }
        }
        return matriz
    }

    @Preview
    @Composable
    fun PreviewPregunta() {
        val contexto = LocalContext.current

        //ComposablePreguntaFantasma(p =Utils.getRandomQuestion(contexto), onPreguntaSuccess = {}, onPreguntaFail = {})
        ComposableExito({})
    }

    @Composable
    fun ComposablePregunta(p: Pregunta, onPreguntaSuccess: () -> Unit, onPreguntaFail: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_question_mark_24),
                contentDescription = "question",
                modifier = Modifier.size(200.dp),
                tint = Color.Red
            )
            Text(
                text = p.pregunta,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                if(validaPregunta(p.opciones.first(),p)){
                    onPreguntaSuccess.invoke()
                }
                else{
                    onPreguntaFail.invoke()
                }

            }) {
                Text(text = p.opciones.first())
            }
            Button(onClick = {
                if(validaPregunta(p.opciones.last(),p)){
                    onPreguntaSuccess.invoke()
                }
                else{
                    onPreguntaFail.invoke()
                }
            }) {
                Text(text = p.opciones.last())
            }
        }
    }

    @Composable
    fun ComposablePreguntaFantasma(p: Pregunta, onPreguntaSuccess: () -> Unit, onPreguntaFail: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.fantasma)
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever,
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = p.pregunta,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                if(validaPregunta(p.opciones.first(),p)){
                    onPreguntaSuccess.invoke()
                }
                else{
                    onPreguntaFail.invoke()
                }

            }) {
                Text(text = p.opciones.first())
            }
            Button(onClick = {
                if(validaPregunta(p.opciones.last(),p)){
                    onPreguntaSuccess.invoke()
                }
                else{
                    onPreguntaFail.invoke()
                }
            }) {
                Text(text = p.opciones.last())
            }
        }
    }

    @Composable
    fun ComposableExito(onExitClick:()->Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_cake_24),
                contentDescription = "question",
                modifier = Modifier.size(200.dp),
                tint = Color.Magenta
            )
            Text(
                text = "Felicidades haz encontrado el pastel y la salida con el!!!",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                onExitClick.invoke()
            }) {
                Text(text = "Salir")
            }
        }
    }

    fun validaPregunta(opcion: String, p: Pregunta): Boolean  {
        Log.d("pregunta","opcion $opcion == ${p.respuestaCorrecta} = ${opcion == p.respuestaCorrecta}")
        return opcion == p.respuestaCorrecta
    }
}