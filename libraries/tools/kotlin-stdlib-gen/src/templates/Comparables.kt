package templates

import templates.Family.*

fun comparables(): List<GenericFunction> {
    val templates = arrayListOf<GenericFunction>()

    templates add f("atLeast(minimumValue: SELF)") {
        only(Primitives, Generic)
        only(*numericPrimitives)
        returns("SELF")
        typeParam("T: Comparable<T>")
        doc {
            """
            Ensures that this value is not less than the specified [minimumValue].

            @returns this value if it's greater than or equal to the [minimumValue] or the [minimumValue] otherwise.
            """
        }
        body {
            """
            return if (this < minimumValue) minimumValue else this
            """
        }

    }

    templates add f("atMost(maximumValue: SELF)") {
        only(Primitives, Generic)
        only(*numericPrimitives)
        returns("SELF")
        typeParam("T: Comparable<T>")
        doc {
            """
            Ensures that this value is not greater than the specified [maximumValue].

            @returns this value if it's greater than or equal to the [maximumValue] or the [maximumValue] otherwise.
            """
        }
        body {
            """
            return if (this > maximumValue) maximumValue else this
            """
        }
    }

    templates add f("coerce(range: TRange)") {
        only(Primitives, Generic)
        only(*numericPrimitives)
        returns("SELF")
        typeParam("T: Comparable<T>")
        doc {
            """
            Ensures that this value lies in the specified [range].

            @returns this value if it's in the [range], or range.start if this value is less than range.start, or range.end if this value is greater than range.end.
            """
        }
        body {
            """
            return if (this < range.start) range.start else if (this > range.end) range.end else this
            """
        }
    }

    templates add f("coerce(minimumValue: SELF?, maximumValue: SELF?)") {
        only(Primitives, Generic)
        only(*numericPrimitives)
        returns("SELF")
        typeParam("T: Comparable<T>")
        doc {
            """
            Ensures that this value lies in the specified range [minimumValue]..[maximumValue].

            @returns this value if it's in the range, or [minimumValue] if this value is less than [minimumValue], or [maximumValue] if this value is greater than [maximumValue].
            """
        }
        body {
            """
            if (minimumValue != null && this < minimumValue) return minimumValue
            if (maximumValue != null && this > maximumValue) return maximumValue
            return this
            """
        }

    }

    return templates
}