package org.example

class TestInline {
    @BuilderProperty
    inline fun <reified T> hello(t: T) {
        println(t)
    }

    fun world() {
        hello("hi")
    }
}