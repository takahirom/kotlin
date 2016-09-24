import java.lang.reflect.*
import java.util.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>List<!>

fun foo(
        <!UNUSED_PARAMETER!>p1<!>: Array<String> /* should be resolved to kotlin.Array */,
        <!UNUSED_PARAMETER!>p2<!>: <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>List<!><String> /* should be resolved to java.util.List */) { }
