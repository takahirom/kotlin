import java.util.ArrayList

fun foo(p: java.util.<!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>List<!><String>) {
    p.iterator(); // forcing resolve of java.util.List.iterator()

    ArrayList<String>().iterator(); // this provoked exception in SignaturesPropagationData
}
