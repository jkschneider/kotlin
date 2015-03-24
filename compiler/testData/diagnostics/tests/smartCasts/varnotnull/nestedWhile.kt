class Bar {
    fun next(): Bar? {
        if (2 == 4)
            return this
        else
            return null
    }
}

fun foo(): Bar {
    var x: Bar? = Bar()
    var y: Bar? = Bar()
    while (x != null) {
        // Here call is unsafe because of the inner loop
        y<!UNSAFE_CALL!>.<!>next()
        while (y != null) {
            if (x == y)
                return <!DEBUG_INFO_SMARTCAST!>x<!>
            y = <!DEBUG_INFO_SMARTCAST!>y<!>.next()
        }
        x = <!DEBUG_INFO_SMARTCAST!>x<!>.next()
    }
    return Bar()
}