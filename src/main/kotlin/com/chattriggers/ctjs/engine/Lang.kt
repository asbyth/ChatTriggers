package com.chattriggers.ctjs.engine

enum class Lang(
        val langName: String,
        val extension: String,
        val graalName: String,
        private val _providedLibsName: String) {
    JS("JavaScript", "js", "js", "providedLibs"),
    PY("Python", "py", "python", "provided_libs"),
    RB("Ruby", "rb", "ruby", "provided_libs"),
    R("R", "R", "R", "provided_libs");

    val providedLibsName: String
        get() {
            return "$_providedLibsName.$extension"
        }
}