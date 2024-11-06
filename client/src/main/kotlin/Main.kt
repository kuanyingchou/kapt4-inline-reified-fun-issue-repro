package org.example

import com.example.lib.MyClass
import com.example.lib.MyInterface
import com.example.lib.MyLibAnnotation

class A: @MyAnnotation MyInterface {
    var _name: String? = null

    @BuilderProperty
    fun setName(s: String) { _name = s }

    fun f(): MyClass = TODO()
}

@Target(AnnotationTarget.TYPE)
annotation class MyAnnotation(val values: Array<String> = ["1", "2", "3"])

