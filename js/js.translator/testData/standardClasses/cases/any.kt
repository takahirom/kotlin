package foo

class A : Any()

fun box(): String {
    val x: Any? = Any()
    if (x !is Any) return "fail1"

    val a: Any? = A()
    if (a !is Any) return "fail2"

    val array: Any? = arrayOf(1, 2, 3)
    if (array !is Any) return "fail3"

    val naked: Any? = createNakedObject()
    if (naked is Any) return "fail4"

    return "OK"
}

fun createNakedObject() = js("Object.create(null)")