package com.example.clipkeyboard

object EmojiCodeTranslator {

    private val digitWords = listOf(
        "zero", "one", "two", "three", "four",
        "five", "six", "seven", "eight", "nine"
    )
    private val digitMap: Map<String, String> =
        digitWords.mapIndexed { index, word -> word to index.toString() }.toMap()

    private val regionalIndicatorRegex = Regex("^regional_indicator_([a-z])$")
    private val tokenRegex = Regex(":([a-zA-Z0-9_]+):")

    fun translate(input: String): String {
        val matches = tokenRegex.findAll(input).toList()
        if (matches.isEmpty()) return input

        val sb = StringBuilder()
        for (m in matches) {
            val token = m.groupValues[1].lowercase()
            val digit = digitMap[token]
            if (digit != null) {
                sb.append(digit)
                continue
            }
            val letterMatch = regionalIndicatorRegex.find(token)
            if (letterMatch != null) {
                sb.append(letterMatch.groupValues[1])
                continue
            }
        }
        return sb.toString()
    }

    fun looksTranslatable(input: String?): Boolean {
        if (input.isNullOrBlank()) return false
        if (!tokenRegex.containsMatchIn(input)) return false
        return translate(input).isNotEmpty()
    }
}
