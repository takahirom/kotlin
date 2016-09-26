class Foo {
    <!UNNECESSARY_LATEINIT!>lateinit<!> var bar: String
    var baz: Int

    init {
        baz = 1
    }

    init {
        bar = ""
    }
}