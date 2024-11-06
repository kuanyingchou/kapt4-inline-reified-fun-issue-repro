package com.example.lib

@Target(AnnotationTarget.TYPE)
annotation class MyLibAnnotation(val values: Array<String> = ["1", "2", "3"])

interface MyInterface
class MyClass : @MyLibAnnotation MyInterface {
    fun setName(s: String) { }
}