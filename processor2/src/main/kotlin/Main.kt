package org.example

fun main() {
    println("Hello World!")
}

class A {
    val b: @MyTypeAnnotation("a", "b") String = "hi"
    val a by lazy {
        "hello"
    }
}