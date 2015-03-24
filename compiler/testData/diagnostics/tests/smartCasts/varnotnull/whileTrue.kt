fun x(): Boolean { return true }

public fun foo(pp: String?): Int {
    var p = pp
    while(true) {
        p!!.length()
        if (x()) break
        p = null
    }
    // Smart cast is NOT possible here
    return p<!UNSAFE_CALL!>.<!>length()
}