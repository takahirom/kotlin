// !API_VERSION: 1.0

@SinceKotlin("1.1")
open class Foo

class Bar @SinceKotlin("1.1") constructor()


fun t1(): <!API_NOT_AVAILABLE!>Foo<!> = <!UNRESOLVED_REFERENCE!>Foo<!>()

fun t2() = object : <!UNRESOLVED_REFERENCE, DEBUG_INFO_UNRESOLVED_WITH_TARGET!>Foo<!>() {}

fun t3(): Bar? = <!UNRESOLVED_REFERENCE!>Bar<!>()
