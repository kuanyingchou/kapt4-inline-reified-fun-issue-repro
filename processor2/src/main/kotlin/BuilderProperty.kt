package org.example

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BuilderProperty

@Target(AnnotationTarget.TYPE)
annotation class MyTypeAnnotation(vararg val v: String)