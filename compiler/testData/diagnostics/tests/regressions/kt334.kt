// !CHECK_TYPE

import java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Comparable<!> as Comparable

fun f(c: <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Comparable<!><*>) {
    checkSubtype<kotlin.Comparable<*>>(<!TYPE_MISMATCH!>c<!>)
    checkSubtype<java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Comparable<!><*>>(c)
}
