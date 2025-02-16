/**
 * MainActivity.kt - 1 of 2 files for ECE558 project 1 - calculator
 *
 * @author	Marco Martinez Corral  (marcom@pdx.edu)
 * @date    11-FEB-2025/15-FEB-2025
 *
 * @brief
 * MainActivity.kt implements composables for a calculator app, and implements the Calculator object in Calculator.kt
 * The operand functions in Calculator.kt are called by the composables in MainActivity.kt

 * @detail
 * composable implementation, calling on the Calculator class created in Calculator.kt
 * Also sets functionality for equals button, clear button, as well as creating a number
 * pad for the main UI of the app
 *
 * Revision History
 * ----------------
 * 15-Jan-2024	RK	Created this starter code template
 *
 *
 * @note:	<Good place to put notes our acknowledge your sources>
 *     ChatGPT-4 used for composable help and generation
 *     Gemini used for font and colors (using built in functionality on android studio)
 *     did not implement much of this hackernoon.com on the app itself, but it helped me learn
 *     the composable basics
 *     https://hackernoon.com/the-ultimate-jetpack-compose-cheat-sheet
 */




package com.example.twoplustwomachine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.twoplustwomachine.ui.theme.TwoPlusTwoMachineTheme

//rectangle button imports - this was a bit of a headache trying to get setup right
//import androidx.compose.foundation.shape.RectangleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.size

import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

//tried to change font to verdana, but it ended up messing the composable formatting
//behold.....the ruins of my failed attempt
//import androidx.compose.ui.text.font.Font
//import androidx.compose.ui.text.font.FontFamily
//val verdanaFontFamily = FontFamily(
//    Font(R.font.verdana, FontWeight.Normal),
//    Font(R.font.verdana_bold, FontWeight.Bold)
//)

//Note: this wasn't working, so I put this main() function up at the top

fun main() {
    // Add your variable declarations here

    /** Add this code to one of your source files **/
    // Greet the user and display the current working directory
    println("Hello, Kotlin User!")

    // need to handle exceptions if any of the system calls fail
    try {
        // Get the current working directory using Kotlin's Path API
        val currentWorkingDirectory = Path("").absolutePathString()

        // Display the working directory
        println("Current working directory: $currentWorkingDirectory")
    } catch (e: Exception) {
        // Handle any errors (similar to perror in C)
        System.err.println("Error: Unable to retrieve the current working directory.")
        System.err.println("Exception message: ${e.message}")
    }

    // ADD YOUR SOURCE CODE THERE
}// main()

//ACTUAL CODE STARTS HERE


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorScreen()
        }
    }
}

/* Calculator Screen composable. It sets the text, background color, and padding for the main UI
* it also implements the clear function to get rid of text on input/result  */
@Composable
fun CalculatorScreen() {
    val backgroundColor = Color(0xFFFAC9D1) //set background to match my terminal on my laptop
    val textColor = Color.Black
//input for buttons and calculator class function
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
//added to clear state of input and result
    val onClear = {
        input = ""
        result = ""
    }
    /*consulted ChatGPT-4 for help on the modifiers and outline for this a lot. I described the top/bottom interface
    * i wanted it to generate, and then made modifications for rectangular buttons and colors. Decided to manually
    * add the Hex color code instead of setting a theme since it would be so few colors */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), //Padding
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Two Plus Two Machine",
            style = TextStyle(
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                //fontFamily = verdanaFontFamily //Originally tried to set font with Gemini, but it didn't work
            )        )
    }
        //2 fields for input and result. Bigger text for result, smaller for input. Wanted to have a
        //more "calculator" looking interface, as opposed to text fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = result,
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = textColor),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                textStyle = TextStyle(fontSize = 24.sp, color = textColor)
            )
        }

        //adding row above the "4 banger" buttons. Since there are only 3, it was not easy to add it
        //with the rest of the number pad. See "NumberPad" composable below to see what I mean
        Column {
            FunctionRow(listOf("√", "ln", "^2", "%"), input) { input = it }
            //clear button implementation
            NumberPad(input, onInputChange = { input = it }, onClear = onClear)
            EqualsButton(input) { newResult, newInput ->
                result = newResult
                input = newInput //input defined for Calculator evaluateExpression
            }
        }
    }
}

/*this composable sets a row of function keys defined by FunctionRow:"√", "ln", "^2", "%". */
@Composable
fun FunctionRow(functions: List<String>, input: String, onInputChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        functions.forEach { func ->
            //using teal to differentiate from "4 banger" operations
            CalculatorButton(text = func, color = Color(0xFF008080)) {
                onInputChange(input + func) //input and function are concatenated together
            }
        }
    }
}

/* Equals button to implement evaluateExpression function from Calculator class and effectively end the input*/
@Composable
fun EqualsButton(input: String, onResult: (String, String) -> Unit) {
    val calculator = Calculator()
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        CalculatorButton(text = "=", color = Color(0xFFFF5555)) {
            //Calculator class function to evaluate expression
            val result = calculator.evaluateExpression(input)
            onResult(result, "")
        }
    }
}

//this composable defines the shape of every single button
//square shape, spacing, and size of button.
@Composable
fun CalculatorButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(0.dp),  // Ensures square shape
        modifier = Modifier
            .padding(4.dp)  // Adjust spacing
            .size(70.dp)    // Ensures equal width and height (square)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}


/*this composable defines the number pad. I used a row implementation for the "classic" 4 banger calculator layout
The clear button was added after initial testing.*/
@Composable
fun NumberPad(input: String, onInputChange: (String) -> Unit, onClear: () -> Unit) {
    val rows = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("Clr", "0", ".", "+")
    )

    Column {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { key ->
                    CalculatorButton(
                        text = key,
                        color = when (key) {
                            in listOf("+", "-", "*", "/") -> Color.DarkGray
                            "Clr" -> Color(0xFFFFEB3B)
                            else -> Color(0xFF93E144)
                        }
                    ) {
                        if (key == "Clr") {
                            onClear() // Clears the input and result
                        } else {
                            onInputChange(input + key)
                        }
                    }
                }
            }
        }
    }
}



//asked ChatGPT to add this preview,
// but i ended up realizing i never used it because i kept reloading the app.....
@Preview(showBackground = true)
@Composable
fun PreviewCalculator() {
    CalculatorScreen()
}

