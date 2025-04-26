package com.example.demologplugin

data class LogEntry(
    val timestamp: String,
    val level: LogLevel,
    val tag: String,
    val message: String
) {
    override fun toString(): String {
//        val paddedLevel = String.format("%-5s", level.label)
//        val paddedTag = String.format("%-15s", tag)
        return "[$timestamp] ${level.label}  $tag: $message"
    }
}