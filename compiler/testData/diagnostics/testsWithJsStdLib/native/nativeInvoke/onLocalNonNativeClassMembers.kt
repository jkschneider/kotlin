// !DIAGNOSTICS: -UNUSED_PARAMETER

fun foo() {
    class A {
        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>nativeInvoke
        fun foo()<!> {}

        <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>nativeInvoke
        fun invoke(a: String): Int<!> = 0

        val anonymous = object {
            <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>nativeInvoke
            fun foo()<!> {}

            <!NATIVE_ANNOTATIONS_ALLOWED_ONLY_ON_MEMBER_OR_EXTENSION_FUN!>nativeInvoke
            fun invoke(a: String): Int<!> = 0
        }
    }
}