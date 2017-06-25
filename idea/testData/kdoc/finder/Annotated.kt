open class Foo() {
    @Supress("unused")
    /** Doc for method xyzzy */
    open fun xyzzy(): Int = 0
}

open class Bar(): Foo() {
    override fun xyzzy(): Int = 1
}
