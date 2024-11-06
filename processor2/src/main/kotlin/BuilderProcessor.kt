package org.example

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.annotations


@SupportedAnnotationTypes("org.example.BuilderProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class BuilderProcessor : AbstractProcessor() {

    private fun print(msg: String) {
        processingEnv.messager.printMessage(
            Diagnostic.Kind.NOTE,
            msg
        )
    }

    private fun test(element: TypeElement) {
        print("gyz: element: ${element.simpleName}")
        val metadataAnnotation = element.getAnnotation(Metadata::class.java) ?: return
        val classMetadata = KotlinClassMetadata.readStrict(metadataAnnotation)  as KotlinClassMetadata.Class

        classMetadata.kmClass.supertypes.forEach { superType ->
            superType.annotations.forEach { annotation ->
                print("gyz: annotation: ${annotation.className}, ${annotation.arguments}")
            }
        }
    }

    override fun process(
        annotations: Set<TypeElement?>,
        roundEnv: RoundEnvironment
    ): Boolean {
        print("gyz: call process")
        for (annotation in annotations) {
            val annotatedElements = roundEnv.getElementsAnnotatedWith(annotation)
            annotatedElements.forEach { elem ->
                elem.enclosingElement.enclosedElements.forEach { elem ->
                    if (elem.kind == ElementKind.METHOD) {
                        val function = elem as ExecutableElement

                        val typeUtils = processingEnv.typeUtils
                        (typeUtils.asElement(function.returnType) as? TypeElement)?.let {
                            test(it)
                        }
                    }
                }
                test(elem.enclosingElement as TypeElement)
            }
        }
        return true
    }
}