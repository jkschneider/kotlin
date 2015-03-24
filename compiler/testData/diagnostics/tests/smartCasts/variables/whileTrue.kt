fun x(): Boolean { return true }

public fun foo(pp: Any): Int {
    var p = pp
    while(true) {
        (p as String).length()
        if (x()) break
        p = 42
    }
    // Smart cast is NOT possible here
    return p.<!UNRESOLVED_REFERENCE!>length<!>()
}