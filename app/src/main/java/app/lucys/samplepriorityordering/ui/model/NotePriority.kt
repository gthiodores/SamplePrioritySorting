package app.lucys.samplepriorityordering.ui.model

enum class NotePriority(val power: Int) {
    HIGH(4),
    MEDIUM(3),
    NORMAL(2),
    LOW(1);

    fun label(): String = when (this) {
        HIGH -> "High Priority"
        MEDIUM -> "Medium Priority"
        NORMAL -> "Normal"
        LOW -> "Low Priority"
    }

    fun shift(): NotePriority = when (this) {
        HIGH -> MEDIUM
        MEDIUM -> NORMAL
        NORMAL -> LOW
        LOW -> HIGH
    }
}
