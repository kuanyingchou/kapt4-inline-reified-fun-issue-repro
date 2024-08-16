package org.example

import com.google.auto.service.AutoService
import java.io.IOException
import java.io.PrintWriter
import java.util.function.Consumer
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.ExecutableType
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.signature


@SupportedAnnotationTypes("org.example.BuilderProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class BuilderProcessor : AbstractProcessor() {
    override fun process(
        annotations: Set<TypeElement?>,
        roundEnv: RoundEnvironment
    ): Boolean {
        processingEnv.messager.printMessage(
            Diagnostic.Kind.NOTE,
            "gyz:call process"
        )
        for (annotation in annotations) {
            val annotatedElements = roundEnv.getElementsAnnotatedWith(annotation)
            annotatedElements.forEach { elem ->
                val metadataAnnotation = elem.enclosingElement.getAnnotation(Metadata::class.java)
                val classMetadata = KotlinClassMetadata.readStrict(metadataAnnotation)  as KotlinClassMetadata.Class
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "gyz: "+classMetadata.kmClass.functions.joinToString { it.signature.toString() }
                )
            }

            val (setters, otherMethods) = annotatedElements.partition {
                ((it.asType() as ExecutableType).parameterTypes.size == 1
                        && it.simpleName.toString().startsWith("set"))
            }

            otherMethods.forEach { element ->
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "${element}"
                )
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@BuilderProperty must be applied to a setXxx method "
                            + "with a single argument", element
                )
            }
            if (setters.isEmpty()) {
                continue;
            }
            val className = (setters[0]
                .enclosingElement as TypeElement).qualifiedName.toString()
            val settersMap = setters.map {
                it.simpleName.toString() to
                    (it.asType() as ExecutableType).parameterTypes.first().toString()
            }.toMap()
            writeBuilderFile(className, settersMap)
        }


        return true
    }

    @Throws(IOException::class)
    private fun writeBuilderFile(
        className: String, setterMap: Map<String, String>
    ) {
        var packageName: String? = null
        val lastDot = className.lastIndexOf('.')
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot)
        }

        val simpleClassName = className.substring(lastDot + 1)
        val builderClassName = className + "Builder"
        val builderSimpleClassName = builderClassName
            .substring(lastDot + 1)

        val builderFile = processingEnv.filer
            .createSourceFile(builderClassName)

        PrintWriter(builderFile.openWriter()).use { out ->
            if (packageName != null) {
                out.print("package ")
                out.print(packageName)
                out.println(";")
                out.println()
            }
            out.print("public class ")
            out.print(builderSimpleClassName)
            out.println(" {")
            out.println()

            out.print("    private ")
            out.print(simpleClassName)
            out.print(" object = new ")
            out.print(simpleClassName)
            out.println("();")
            out.println()

            out.print("    public ")
            out.print(simpleClassName)
            out.println(" build() {")
            out.println("        return object;")
            out.println("    }")
            out.println()

//            setterMap.entries.forEach(Consumer { setter: Map.Entry<String, String> ->
//                val methodName = setter.key
//                val argumentType = setter.value
//
//                out.print("    public ")
//                out.print(builderSimpleClassName)
//                out.print(" ")
//                out.print(methodName)
//
//                out.print("(")
//
//                out.print(argumentType)
//                out.println(" value) {")
//                out.print("        object.")
//                out.print(methodName)
//                out.println("(value);")
//                out.println("        return this;")
//                out.println("    }")
//                out.println()
//            })
            out.println("}")
        }
    }
}