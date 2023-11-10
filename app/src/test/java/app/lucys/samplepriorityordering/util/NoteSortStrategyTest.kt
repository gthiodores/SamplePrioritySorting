package app.lucys.samplepriorityordering.util

import app.lucys.samplepriorityordering.ui.model.Note
import app.lucys.samplepriorityordering.ui.model.NotePriority
import app.lucys.samplepriorityordering.ui.util.PinnedNoteSort
import app.lucys.samplepriorityordering.ui.util.byPriorityAndTimeSort
import app.lucys.samplepriorityordering.ui.util.byTimeAndPrioritySort
import kotlinx.datetime.Clock
import org.junit.Test
import kotlin.time.Duration.Companion.hours

class NoteSortStrategyTest {

    private val time = Clock.System.now()

    private val item1 = Note(
        priority = NotePriority.HIGH,
        content = "Note 1",
        time = time,
    )
    private val item2 = Note(
        priority = NotePriority.MEDIUM,
        content = "Note 2",
        time = time,
    )
    private val item3 = Note(
        priority = NotePriority.HIGH,
        content = "Note 3",
        time = time.minus(1.hours),
    )
    private val item4 = Note(
        priority = NotePriority.MEDIUM,
        content = "Note 4",
        time = time.minus(1.hours),
    )
    private val item5 = Note(
        priority = NotePriority.LOW,
        content = "Note 5",
        time = time.minus(1.hours),
    )
    private val list = listOf(item1, item2, item3, item4, item5)

    @Test
    fun should_sort_ascending_time_then_descending_power() {
        val expectedOutput = listOf(item3, item4, item5, item1, item2)
        val output = byTimeAndPrioritySort().execute(list)

        assert(expectedOutput == output)
    }

    @Test
    fun should_sort_descending_power_then_ascending_time() {
        val expectedOutput = listOf(item3, item1, item4, item2, item5)
        val output = byPriorityAndTimeSort().execute(list)

        assert(expectedOutput == output)
    }

    @Test
    fun should_pin_then_sort_descending_power_ascending_time() {
        val expectedOutput = listOf(item5, item3, item1, item4, item2)
        val output = PinnedNoteSort(pinned= NotePriority.LOW).execute(list)

        assert(expectedOutput == output)
    }

}