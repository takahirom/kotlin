// WITH_RUNTIME
// INTENTION_TEXT: "Replace with 'any{}'"
fun foo(list: List<String>) {
    var result = 0
    <caret>for (s in list) {
        if (s.length > 0) {
            result = 1
            break
        }
    }
}