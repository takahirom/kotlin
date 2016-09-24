// !CHECK_TYPE

package a

import java.util.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Iterator<!>
import java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Comparable<!> as Comp

fun bar(any: Any): java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Iterable<!><Int>? {
    val <!UNUSED_VARIABLE!>a<!>: java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Comparable<!><String>? = null
    val <!UNUSED_VARIABLE!>b<!>: Iterable<<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Integer<!>>
    val <!UNUSED_VARIABLE!>c<!> : <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Iterator<!><String>? = null

    if (any is <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Iterator<!><*>) {
        checkSubtype<<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Iterator<!><*>>(<!DEBUG_INFO_SMARTCAST!>any<!>)
    }
    any as <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Iterator<!><*>
    return null
}