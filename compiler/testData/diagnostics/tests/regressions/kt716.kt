// KT-716 Type inference failed

class TypeInfo<T>

fun <T> typeinfo() : TypeInfo<T> = null <!CAST_NEVER_SUCCEEDS!>as<!> TypeInfo<T>

fun <T> TypeInfo<T>.getJavaClass() : java.lang.Class<T> {
    val t : java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Object<!> = this as java.lang.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Object<!>
    return <!UNCHECKED_CAST!>t.getClass() as java.lang.Class<T><!> // inferred type is Object but Serializable was expected
}

fun <T> getJavaClass() = typeinfo<T>().getJavaClass()

fun main(args : Array<String>) {
    System.out.println(getJavaClass<String>())
}
