// !API_VERSION: 1.0

@SinceKotlin("1.1")
fun f() {}

@SinceKotlin("1.1")
var p = Unit


fun t1() = <!UNRESOLVED_REFERENCE!>f<!>()

fun t2() = <!UNRESOLVED_REFERENCE!>p<!>

fun t3() { <!UNRESOLVED_REFERENCE!>p<!> = Unit }
