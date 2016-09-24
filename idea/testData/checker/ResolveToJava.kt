import java.*
import java.util.*
import <error>utils</error>.*

import java.io.PrintStream
import <warning>java.lang.Comparable</warning> as Com

fun <T> checkSubtype(t: T) = t

val l : MutableList<in Int> = ArrayList<Int>()

fun test(<warning>l</warning> : List<Int>) {
  val <warning>x</warning> : java.<error>List</error>
  val <warning>y</warning> : List<Int>
  val <warning>b</warning> : java.lang.<warning>Object</warning>
  val <warning>a</warning> : java.util.<warning>List</warning><Int>
  val <warning>z</warning> : java.<error>utils</error>.<error>List</error><Int>

  val <warning>f</warning> : java.io.File? = null

  Collections.<error><error>emptyList</error></error>
  Collections.<error>emptyList<Int></error>
  Collections.emptyList<Int>()
  Collections.<error>emptyList</error>()

  checkSubtype<Set<Int>?>(Collections.singleton<Int>(1))
  Collections.singleton<Int>(<error>1.0</error>)

  <error>List</error><Int>


  val <warning>o</warning> = "sdf" as <warning>Object</warning>

  try {
    // ...
  }
  catch(e: Throwable) {
    System.out.println(e.message)
  }

  PrintStream("sdf")

  val c : <warning>Com</warning><Int>? = null

  checkSubtype<java.lang.<warning>Comparable</warning><Int>?>(c)

//  Collections.sort<Integer>(ArrayList<Integer>())
}


