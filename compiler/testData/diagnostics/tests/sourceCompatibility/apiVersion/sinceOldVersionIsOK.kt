// !API_VERSION: 1.1

@SinceKotlin("0.9")
fun ok1() {}

@SinceKotlin("1.0")
fun ok2() {}

@SinceKotlin("1.1")
fun ok3() {}


fun t1() = ok1()
fun t2() = ok2()
fun t3() = ok3()
