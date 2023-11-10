package app.lucys.samplepriorityordering.ui.util

import app.lucys.samplepriorityordering.ui.model.Note
import app.lucys.samplepriorityordering.ui.model.NotePriority

interface NoteSortStrategy {
    fun execute(items: List<Note>): List<Note>
}

/**
 * Sorts the notes using the provided comparator.
 * @param comparator the comparator to sort
 */
class ComparatorNoteSort(
    private val comparator: Comparator<Note>
) : NoteSortStrategy {
    override fun execute(items: List<Note>): List<Note> = items.sortedWith(comparator)
}

/**
 * Sorts note by time then if there's a note with equal time, will sort by power.
 *
 * Example:
 * ```
 * val item1 = Note(priority = NotePriority.HIGH, time = Clock.System.now())
 * val item2 = Note(priority = NotePriority.MEDIUM, time = Clock.System.now())
 * val item3 = Note(priority = NotePriority.LOW, time = Clock.System.now().minus(1.hour))
 *
 * // [item3, item1, item2]
 * val result = byTimePrioritySort.execute(listOf(item1, item2, item3))
 * ```
 */
fun byTimeAndPrioritySort(): NoteSortStrategy =
    compareBy<Note> { it.time }
        .then(compareByDescending { it.priority.power })
        .let(::ComparatorNoteSort)

/**
 * Sorts note by power then if there's a note with equal power, will sort by time.
 * Example:
 * ```
 * val item1 = Note(priority = NotePriority.HIGH, time = Clock.System.now())
 * val item2 = Note(priority = NotePriority.MEDIUM, time = Clock.System.now())
 * val item3 = Note(priority = NotePriority.LOW, time = Clock.System.now().minus(1.hour))
 *
 * // [item1, item2, item3]
 * val result = byTimePrioritySort.execute(listOf(item1, item2, item3))
 * ```
 */
fun byPriorityAndTimeSort(): NoteSortStrategy =
    compareByDescending<Note> { it.priority.power }
        .then(compareBy { it.time })
        .let(::ComparatorNoteSort)

/**
 * Pin a note with [pinned] priority to the top of the list and sort them using [sortStrategy]
 * Example:
 * ```
 * val item1 = Note(priority = NotePriority.HIGH, time = Clock.System.now())
 * val item2 = Note(priority = NotePriority.MEDIUM, time = Clock.System.now())
 * val item3 = Note(priority = NotePriority.HIGH, time = Clock.System.now().minus(1.hour))
 * val item4 = Note(priority = NotePriority.MEDIUM, time = Clock.System.now().minus(1.hour))
 *
 * val strategy = PinnedSortStrategy(pinned = NotePriority.MEDIUM)
 *
 * // [item4, item2, item3, item 1]
 * val result = strategy.execute(listOf(item1, item2, item3, item4))
 * ```
 *
 * @param pinned the priority to pin
 * @param sortStrategy the strategy to sort the list, defaults to **byPriorityAndTimeSort()**
 * @see byPriorityAndTimeSort
 */
class PinnedNoteSort(
    private val pinned: NotePriority,
    private val sortStrategy: NoteSortStrategy = byPriorityAndTimeSort()
) : NoteSortStrategy {

    private val _collector = mutableListOf<Note>()

    override fun execute(
        items: List<Note>,
    ): List<Note> {
        _collector.clear()

        val (important, other) = items.partition { it.priority == pinned }
        _collector.addAll(sortStrategy.execute(important))
        _collector.addAll(sortStrategy.execute(other))

        return _collector.toList()
    }
}
