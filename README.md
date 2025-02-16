<b> ECE 558 Project 1 Calculator </b>

<img width="193" alt="Screenshot 2025-02-15 at 11 28 13 PM" src="https://github.com/user-attachments/assets/eb01cb52-4b53-44a5-ae22-512180b27191" />

https://www.youtube.com/watch?v=rskXqNcv7A4 - Live Demo

This project implements a scientific calculator Android
application using Kotlin. The 8 functions implemented are
addition, subtraction, multiplication, division, square root,
natural logarithm, percentage, and squaring a number.


<img width="467" alt="Screenshot 2025-02-16 at 2 00 30 AM" src="https://github.com/user-attachments/assets/6a608daa-bc23-40a8-bba7-72cda8f2652d" />

The calculator employs mutable lists of Strings and doubles. The application employs 2 Kotlin
files: MainActivity.kt, where the composables are defined, and Calculator.kt, where the
calculator input, mathematical operations, and output are defined using a class. The logical
path of the Calculator.kt class is as follows:

Check whether or not the expression is empty or not. If not empty, "clean" the expression by removing spaces, and making sure that only
the defined characters for the mathematical operations(in string form) are present.
Parse through the list of strings to look for mathematical operators (only other
characters available are numbers).
Once the mathematical operators are found, the string is evaluated for numbers. If
numbers are found (still in string form), it converts them to Double types, and also
assigns the corresponding mathematical expression based on the non-number
character.
The expression is then evaluated, and the result pushed to the stack of Double
types; the stack is then cleared for a new expression to be pushed and evaluated. If
the clear button is pushed, the stack is cleared of values, and new numbers can be
pushed to it.

All UI elements were implemented using Andriod Composables; the input for the calculator was
defined by composable buttons arranged in a 2 separate grids, a top one for scientific
functions, and a bottom one for the number pad, basic mathematical operations, and a clear
button, as well as the equals button at the bottom of the screen.



