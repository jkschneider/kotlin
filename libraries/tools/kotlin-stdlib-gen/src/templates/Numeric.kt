package templates

import templates.Family.*

fun numeric(): List<GenericFunction> {
    val templates = arrayListOf<GenericFunction>()

    templates add f("sum()") {
        exclude(Strings)
        doc { "Returns the sum of all elements in the collection." }
        returns("SUM")
        body {
            """
            val iterator = iterator()
            var sum: SUM = ZERO
            while (iterator.hasNext()) {
                sum += iterator.next()
            }
            return sum
            """
        }
    }

    templates add f("average()") {
        exclude(Strings)
        doc { "Returns an average value of elements in the collection"}
        returns("Double")
        body {
            """
            val iterator = iterator()
            var sum: Double = 0.0
            var count: Int = 0
            while (iterator.hasNext()) {
                sum += iterator.next()
                count += 1
            }
            return sum / count
            """
        }
    }

    return templates
}
