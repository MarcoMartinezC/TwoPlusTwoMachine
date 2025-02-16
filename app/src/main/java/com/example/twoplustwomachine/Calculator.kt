/**
 * Calculator.kt - 2 of 2 files for ECE558 project 1 - calculator
 *
 * @author	Marco Martinez Corral  (marcom@pdx.edu)
 * @date    11-FEB-2025/15-FEB-2025
 *
 * @brief
 * Calculator.kt defines the Calculator object, which is used to implement mathematical operations for the calculator app.
 * subtraction, multiplication, division, exponentiation, square root, and natural logarithm are supported.
 * Errors for missing operators and for missing numbers are also handled with respective functions.
 *
 * @detail
 * Implements a calculator class, and defines the mathematical operations.
 * A string is built from the inputs, and parsed for operators. Based on this, the expression is built,
 * and then pushed onto a stack, where it is evaluated.
 *
 * Revision History
 * ----------------
 * 15-Jan-2024	RK	Created this starter code template
 *
 *
 * @note:	<Good place to put notes our acknowledge your sources>
 *     ChatGPT-4 used for syntax checking, regex usage, and error handling help.
 *     Online resources:
 *     https://kotlinlang.org/docs/operator-overloading.html
 *     https://medium.com/@ramadan123sayed/understanding-regex-in-kotlin-a-comprehensive-guide-a5a55e069367
 *     https://www.tpointtech.com/kotlin-mutablelist-mutablelistof
 *     https://medium.com/wearejaya/data-structures-in-kotlin-stack-queue-partv-31771dafa89
 */


package com.example.twoplustwomachine

//needed for mathematical operations. Wildcard import to use all functions.
import kotlin.math.*

//implementing calculator class.
class Calculator {
    //Android studio/Gemini suggested "Function 'isValidExpression' could be private" due to the expression
    //evaluation only needing to happen before the expression is actually evaluated. I added the private property
    //as a result

    private fun isValidExpression(tokens: List<String>): Boolean { //boolean since it needs to check validity
        if (tokens.isEmpty()) return false //empty expression cannot be evaluated, function returns false
        //operators for math values for "four banger" functions
        val operators = setOf("+", "-", "*", "/")

        //I chose natural log, and square exponential (power of 2, ^2) for my own scientific functions
        //they only require a single operand, along with sqrt and %,
        // and need a condition for only a single number (error catching)
        val single = setOf("ln", "√", "^2", "%")

        //setting index to run through a token, in this case the operator or a number
        for (i in tokens.indices) {
            val token = tokens[i]
            //check for character/token in expression. Invalid if expression ends in operator

            if (token in operators) {
                val nextToken = tokens[i + 1]
                //check for missing number after operator
                if (nextToken in operators) {
                    return false //Number required after operator, cannot end with operator
                }
            }

            //single operator, will throw error if it's at end, except for ^2
            //without this, ln/√ would return the number without any mathematical operations applied
            if (token in single && i == tokens.lastIndex) {
                return false //false if there is no number after single operator
            }
        }
        return true //valid operator
    }

    //function for mathematical evaluation
    fun evaluateExpression(expression: String): String {
        //implemented a try/catch block for error handling
        //2 error cases: missing operator or missing number
        //single operator, as defined above, return an error if they are listed at the end
        return try {
            //online examples recommended removing spaces in the "raw" expression (evaluated before composable implement)
            //I used ChatGPT for help on this, it took me a bit to realize the math expression needed to be "sanitized"
            //prior to being evaluated, since all of these numbers are Double types
            val cleanedExpression = expression.replace(" ", "") //Remove spaces in mathematical expression
            val tokens = tokenizeExpression(cleanedExpression)
            //check to see if expression is valid before evaluating
            if (!isValidExpression(tokens)) {
                //these are the main errors thrown on the screen to the user
                return if (tokens.last() in setOf("ln", "√"))
                    "Error - Missing Number for Function" //missing number for function
                //error kept coming up unless I added this "generic" catch exception line. I just added "error" as a generic
                else
                    "Error" //need to have 2 inputs for math operation
            }

            val result = evaluateTokens(tokens)
            if (result.isFinite()) result.toString() else "Error"
        }
        catch (e: Exception) {
            "Error - Needs 2 operators"
        }
    }

    /* building upon the regex usage from the RPN calculator; looking for ln, +, -, *, /, ^, √
    * finds character using regex, and adds it to a list of strings, which is then used to evaluate the string
    * to a double in the evaluate tokens function below. I tried NOT using a regex function, and having cases for
    * each operator instead, but it ended up being more complicated, since I had to account for a case for every single
    * operator. Ultimately, it was easier to implement a string, then use the character as the operator on a per case
    * basis. Since the user can ONLY input what's on the buttons, they wont be able to do weird characters, anyway.

    * * Negative number input was handled by checking for the negative symbol to be in the first position of the
    * string of tokens. If -, and ONLY -, is found, then it is appended to the a mutable list of string types  */

    private fun tokenizeExpression(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        //aforementioned regex from RPN calculator
        val regex = """(ln|\d+\.\d+|\d+|[+\-*/^√%])""".toRegex()
        //finds all expressions with the regex case
        val rawTokens = regex.findAll(expression).map { it.value }.toList()

        var i = 0
        while (i < rawTokens.size) {
            val token = rawTokens[i]
            //case for % operator
            //checks for % as the token, but needs to end expression
            if (token == "%" && tokens.isNotEmpty() && tokens.last().isNumber()) {
                tokens[tokens.lastIndex] = (tokens.last().toDouble() / 100).toString()
            }

            //negative number case
            //checks for - at the beginning of expression
            else if (token == "-" && (i == 0 || rawTokens[i - 1] in listOf(
                    "+",
                    "-",
                    "*",
                    "/",
                    "^",
                    "(",
                    "√",
                    "ln"
                ))
            ) {
                //append negative sign to next token in string
                if (i + 1 < rawTokens.size && rawTokens[i + 1].isNumber()) {
                    tokens.add("-" + rawTokens[i + 1])
                    i++ //skip token after negative sign since it's been appended
                } else {
                    tokens.add(token) //add the negative sign separately if it's not followed by a number
                }
            } else {
                tokens.add(token)
            }
            //increment counter
            i++
        }
        return tokens

    }

    /*
tokens set for numbers, which are Doubles. Characters found from the regex in tokenizeExpression() are used for
the case, which is a mathematical operation
*/
    //check for number
    private fun String.isNumber() = this.toDoubleOrNull() != null

    private fun evaluateTokens(tokens: List<String>): Double {
        val stack = mutableListOf<Double>()
        //mathematical operation being done once input is done
        //nullable to start out with since it literally will be empty when the user opens the app up
        //or hits clear
        var currentOp: String? = null
        //counter to check for size of operation. it ended up being way harder than I thought to restrict to ONLY 2
        //number inputs, since the user could spam a bunch of numbers and mess things up. Here, the user can do as
        //many numbers as they desire. Math, after all, is limitless.............................
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            when {
                token.isNumber() -> {
                    //if input is number, and not an operator sign, it will convert it to Double
                    val num = token.toDouble()
                    when (currentOp) {
                        //android studio did not like removeLast, and I had to use removeAt instead for compatibility
                        "+" -> stack.add(stack.removeAt(stack.size - 1) + num)
                        "-" -> stack.add(stack.removeAt(stack.size - 1) - num)
                        "*" -> stack.add(stack.removeAt(stack.size - 1) * num)
                        "/" -> stack.add(stack.removeAt(stack.size - 1) / num)
                        "^" -> stack.add(stack.removeAt(stack.size - 1).pow(2))
                        "%" -> stack.add(stack.removeAt(stack.size - 1) / 100)
                        "√" -> stack.add(sqrt(num))
                        "ln" -> stack.add(ln(num))
                        else -> stack.add(num)
                    }
                    currentOp = null
                }
                //populate operation with the tokens after adding to stack
                else -> {
                    currentOp = token
                }
            }
            //increment counter to run through expression
            i++
        }
        //function return
        return stack.lastOrNull() ?: Double.NaN
    }
}

