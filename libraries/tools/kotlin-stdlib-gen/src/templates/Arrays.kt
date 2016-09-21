package templates

import templates.Family.*

fun arrays(): List<GenericFunction> {
    val templates = arrayListOf<GenericFunction>()

    templates add f("isEmpty()") {
        inline(Inline.Only)
        only(ArraysOfObjects, ArraysOfPrimitives)
        doc { "Returns `true` if the array is empty." }
        returns("Boolean")
        body {
            "return size == 0"
        }
    }

    templates add f("isNotEmpty()") {
        inline(Inline.Only)
        only(ArraysOfObjects, ArraysOfPrimitives)
        doc { "Returns `true` if the array is not empty." }
        returns("Boolean")
        body {
            "return !isEmpty()"
        }
    }


    templates add pval("lastIndex") {
        only(ArraysOfObjects, ArraysOfPrimitives)
        doc { "Returns the last valid index for the array." }
        returns("Int")
        body {
            "get() = size - 1"
        }
    }

    templates add pval("indices") {
        only(ArraysOfObjects, ArraysOfPrimitives)
        doc { "Returns the range of valid indices for the array." }
        returns("IntRange")
        body {
            "get() = IntRange(0, lastIndex)"
        }
    }

    templates add f("contentEquals(other: SELF)") {
        only(ArraysOfObjects, ArraysOfPrimitives)
        jvmOnly(true)
        inline(Inline.Only)
        doc { "TODO" }
        returns("Boolean")
        body {
            "return Arrays.equals(this, other)"
        }
    }

    templates add f("contentEquals(other: SELF, deep: Boolean)") {
        only(ArraysOfObjects)
        jvmOnly(true)
        doc { "TODO" }
        returns("Boolean")
        body {
            "return if (deep) Arrays.deepEquals(this, other) else Arrays.equals(this, other)"
        }
    }

    templates add f("contentToString()") {
        only(ArraysOfObjects, ArraysOfPrimitives)
        jvmOnly(true)
        inline(Inline.Only)
        doc { "TODO" }
        returns("String")
        body {
            "return Arrays.toString(this)"
        }
    }

    templates add f("contentToString(deep: Boolean)") {
        only(ArraysOfObjects)
        jvmOnly(true)
        doc { "TODO" }
        returns("String")
        body {
            "return if (deep) Arrays.deepToString(this) else Arrays.toString(this)"
        }
    }

    templates add f("contentHashCode()") {
        only(ArraysOfObjects, ArraysOfPrimitives)
        jvmOnly(true)
        inline(Inline.Only)
        doc { "TODO" }
        returns("Int")
        body {
            "return Arrays.hashCode(this)"
        }
    }

    templates add f("contentHashCode(deep: Boolean)") {
        only(ArraysOfObjects)
        jvmOnly(true)
        doc { "TODO" }
        returns("Int")
        body {
            "return if (deep) Arrays.deepHashCode(this) else Arrays.hashCode(this)"
        }
    }

    templates addAll PrimitiveType.defaultPrimitives.map { primitive ->
        val arrayType = primitive.name + "Array"
        f("to$arrayType()") {
            only(ArraysOfObjects, Collections)
            buildFamilies.forEach { family -> onlyPrimitives(family, primitive) }
            doc(ArraysOfObjects) { "Returns an array of ${primitive.name} containing all of the elements of this generic array." }
            doc(Collections) { "Returns an array of ${primitive.name} containing all of the elements of this collection." }
            returns(arrayType)
            // TODO: Use different implementations for JS
            body {
                """
                val result = $arrayType(size)
                for (index in indices)
                    result[index] = this[index]
                return result
                """
            }
            body(Collections) {
                """
                val result = $arrayType(size)
                var index = 0
                for (element in this)
                    result[index++] = element
                return result
                """
            }
        }
    }

    return templates
}