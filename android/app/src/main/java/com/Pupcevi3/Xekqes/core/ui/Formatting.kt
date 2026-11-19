package com.Pupcevi3.Xekqes.core.ui

object Formatting {

    fun score(value: Int): String {
        val digits = value.toString()
        if (digits.length <= 3) return digits
        val builder = StringBuilder()
        val offset = digits.length % 3
        digits.forEachIndexed { index, ch ->
            if (index > 0 && index % 3 == offset) builder.append(' ')
            builder.append(ch)
        }
        return builder.toString()
    }

    fun ratio(done: Int, target: Int): String = done.toString() + "/" + target.toString()
}
