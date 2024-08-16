package org.example

class A {
    var _name: String? = null
    @BuilderProperty
    fun setName(s: String) { _name = s }

    fun normalFun() {}

    inline fun inlineFun() {

    }
    inline fun <reified T> inlineReifiedFun(t: T) {

    }
}
