fun doSomething<T>(a: T) {}

val a: String?
    get() = ""

fun main(args: Array<String>) {
    doSomething(a?.<caret>length())
}
