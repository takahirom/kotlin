@file:JvmVersion
package kotlin.collections

public abstract class AbstractMutableCollection<E> protected constructor() : java.util.AbstractCollection<E>() {
    abstract override fun add(element: E): Boolean
}

public abstract class AbstractMutableList<E> protected constructor() : java.util.AbstractList<E>() {
    abstract override fun set(index: Int, element: E): E
    abstract override fun removeAt(index: Int): E
    abstract override fun add(index: Int, element: E)
}

public abstract class AbstractMutableSet<E> protected constructor() : java.util.AbstractSet<E>() {
    // nothing to make abstract, maybe leave it typealias then?
}

public abstract class AbstractMutableMap<K, V> protected constructor() : java.util.AbstractMap<K, V>() {
    abstract override fun put(key: K, value: V): V?
}