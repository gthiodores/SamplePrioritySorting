package app.lucys.samplepriorityordering.ui.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.hours

data class Note(
    val id: UUID = UUID.randomUUID(),
    val content: String = "Lorem Ipsum",
    val priority: NotePriority,
    val time: Instant = Clock.System.now(),
) {
//    fun log(): String = StringBuilder()
//        .appendLine("ID: $id")
//        .appendLine("Content: $content")
//        .appendLine("\t Priority: ${priority.label()}")
//        .appendLine("\t Time: ${time.epochSeconds}")
//        .toString()

    companion object {
        fun createFakes(): List<Note> {
            val time = Clock.System.now()

            val item1 = Note(
                priority = NotePriority.HIGH,
                content = "Note 1",
                time = time,
            )
            val item2 = Note(
                priority = NotePriority.MEDIUM,
                content = "Note 2",
                time = time,
            )
            val item3 = Note(
                priority = NotePriority.HIGH,
                content = "Note 3",
                time = time.minus(1.hours),
            )
            val item4 = Note(
                priority = NotePriority.MEDIUM,
                content = "Note 4",
                time = time.minus(1.hours),
            )
            val item5 = Note(
                priority = NotePriority.LOW,
                content = "Note 5",
                time = time.minus(1.hours),
            )
            val item6 = Note(
                priority = NotePriority.NORMAL,
                content = "Note 6",
                time = time.minus(2.hours),
            )
            return listOf(item1, item2, item3, item4, item5, item6)
        }
    }
}
