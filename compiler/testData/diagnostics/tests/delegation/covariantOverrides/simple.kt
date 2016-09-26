interface IBase {
    fun foo(): Any
}

interface IDerived : IBase {
    override fun foo(): String
}

<!UNSUPPORTED!>class Broken(val b: IBase) : IBase by b, IDerived<!>
