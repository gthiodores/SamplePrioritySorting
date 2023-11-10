package app.lucys.samplepriorityordering.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lucys.samplepriorityordering.ui.model.NotePriority
import app.lucys.samplepriorityordering.ui.util.NoteSortStrategy
import app.lucys.samplepriorityordering.ui.util.PinnedNoteSort
import app.lucys.samplepriorityordering.ui.util.byPriorityAndTimeSort
import app.lucys.samplepriorityordering.ui.util.byTimeAndPrioritySort

@Composable
fun NoteSortDialog(
    onEvent: (NoteSortEvent) -> Unit,
    state: NoteSortDialogState,
) {
    AlertDialog(
        onDismissRequest = { onEvent(NoteSortEvent.CloseWithoutSave) },
        title = { Text("Sort") },
        text = { NoteSortDialogContent(onEvent = onEvent, state = state) },
        confirmButton = {
            Button(onClick = { onEvent(NoteSortEvent.AcceptSort) }) { Text(text = "Sort") }
        },
        dismissButton = {
            Button(onClick = { onEvent(NoteSortEvent.CloseWithoutSave) }) { Text(text = "Cancel") }
        }
    )
}

@Composable
fun NoteSortDialogContent(
    onEvent: (NoteSortEvent) -> Unit,
    state: NoteSortDialogState,
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.option == NoteSortDialogOption.BY_PRIORITY_AND_TIME,
                onClick = { onEvent(NoteSortEvent.SortByPriorityAndTime) },
            )
            Text(NoteSortDialogOption.BY_PRIORITY_AND_TIME.label())
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.option == NoteSortDialogOption.BY_TIME_AND_PRIORITY,
                onClick = { onEvent(NoteSortEvent.SortByTimeAndPriority) },
            )
            Text(NoteSortDialogOption.BY_TIME_AND_PRIORITY.label())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Pinned Priority")
            NoteSortDropDown(onEvent = onEvent, state = state)
        }
    }
}

@Composable
private fun NoteSortDropDown(
    onEvent: (NoteSortEvent) -> Unit,
    state: NoteSortDialogState,
) {
    Box {
        TextButton(
            onClick = { onEvent(NoteSortEvent.ShowPriorityDropDown) },
        ) {
            Text(state.pinPriority?.label() ?: "None")
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "DropDown")
        }

        DropdownMenu(
            expanded = state.isDropDownVisible,
            onDismissRequest = { onEvent(NoteSortEvent.ClosePriorityDropDown) }
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = { onEvent(NoteSortEvent.PinnedPriority(null)) },
            )

            NotePriority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.label()) },
                    onClick = { onEvent(NoteSortEvent.PinnedPriority(priority)) },
                )
            }
        }
    }
}

data class NoteSortDialogState(
    val option: NoteSortDialogOption = NoteSortDialogOption.BY_PRIORITY_AND_TIME,
    val isDropDownVisible: Boolean = false,
    val pinPriority: NotePriority? = null,
) {
    fun toSortStrategy(): NoteSortStrategy = when (pinPriority) {
        null -> option.toStrategy()
        else -> PinnedNoteSort(pinned = pinPriority, sortStrategy = option.toStrategy())
    }
}

enum class NoteSortDialogOption {
    BY_TIME_AND_PRIORITY,
    BY_PRIORITY_AND_TIME;

    fun label(): String = when (this) {
        BY_TIME_AND_PRIORITY -> "Time > Priority"
        BY_PRIORITY_AND_TIME -> "Priority > Time"
    }

    fun toStrategy(): NoteSortStrategy = when (this) {
        BY_TIME_AND_PRIORITY -> byTimeAndPrioritySort()
        BY_PRIORITY_AND_TIME -> byPriorityAndTimeSort()
    }
}

sealed interface NoteSortEvent {
    data object SortByTimeAndPriority : NoteSortEvent
    data object SortByPriorityAndTime : NoteSortEvent
    data object ShowPriorityDropDown : NoteSortEvent
    data object ClosePriorityDropDown : NoteSortEvent
    data class PinnedPriority(val priority: NotePriority?) : NoteSortEvent
    data object CloseWithoutSave : NoteSortEvent
    data object AcceptSort : NoteSortEvent
}