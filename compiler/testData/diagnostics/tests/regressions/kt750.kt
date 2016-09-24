//KT-750 Type inference failed: Constraint violation
fun main(args : Array<String>) {
  var i : Int? = <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Integer<!>.valueOf(100)
  var s : Int? = <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>Integer<!>.valueOf(100)

  val o = i.sure() + s.sure()
  System.out.println(o)
}

fun <T : Any> T?.sure() : T = this!!
