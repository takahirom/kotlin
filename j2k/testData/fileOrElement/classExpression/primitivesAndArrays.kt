object A {
    @JvmStatic fun main(args: Array<String>) {
        println(Unit::class.javaPrimitiveType)
        println(Boolean::class.javaPrimitiveType)
        println(Int::class.javaPrimitiveType)
        println(Double::class.javaPrimitiveType)
        println(IntArray::class.java)
        println(Array<Any>::class.java)
        println(Array<Array<Any>>::class.java)
    }
}
