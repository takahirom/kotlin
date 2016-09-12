package Tests

class A : java.<caret>

// todo: make these packages JAVA_ONLY when they are removed from JS library
// maybe make this test java-only?
// EXIST: util, io
// EXIST_JAVA_ONLY: lang
// ABSENT: fun, val, var, package
