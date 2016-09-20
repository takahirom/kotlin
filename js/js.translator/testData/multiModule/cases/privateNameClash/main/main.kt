package main

import lib.A

class B : A() {
    private var x = 42

    fun bar() = x
}

// NOTE. This test is fragile, it may fail due to unexpected (and correct) changes in algorithm that assigns
// unique identifiers to non-public declarations. However, we don't see any way of doing such test so that
// it won't report false positives eventually. So be patient and just update this test whenever you changed
// algorithm of assigning unique identifiers.
// Please, check thet A.foo and B.foo have different JS names.
private fun checkJsNames(o: dynamic): Boolean = "x_i8qwny\$_0" in o && "x_dqqnpp\$_0" in o

fun box(): String {
    val a = A()
    if (a.foo() != 23) return "fail1: ${a.foo()}"

    val b = B()
    if (b.foo() != 23) return "fail2: ${b.foo()}"
    if (b.bar() != 42) return "fail3: ${b.bar()}"
    if (!checkJsNames(b)) return "fail4"

    return "OK"
}