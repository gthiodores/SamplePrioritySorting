package app.lucys.samplepriorityordering.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lucys.samplepriorityordering.ui.model.Note
import app.lucys.samplepriorityordering.ui.model.NotePriority
import app.lucys.samplepriorityordering.ui.util.byPriorityAndTimeSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID

class NoteListViewModel : ViewModel() {
    private val _notes = MutableStateFlow(
        Note.createFakes() + Note.createFakes()
    )
    private val _strategy = MutableStateFlow(byPriorityAndTimeSort())
    val notes = combine(_notes, _strategy) { items, sort -> sort.execute(items) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), listOf())

    private val _isSortVisible = MutableStateFlow(false)
    private val _previousSortState = MutableStateFlow<NoteSortDialogState?>(null)
    private val _sortState = MutableStateFlow(NoteSortDialogState())
    val sortState =
        combine(_isSortVisible, _sortState) { isVisible, state -> if (isVisible) state else null }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun shiftPriority(id: UUID) {
        _notes.update { old ->
            old.map { if (it.id != id) it else it.copy(priority = it.priority.shift()) }
        }
    }

    fun openSortDialog() {
        val hasPreviousValue = restoreSortBackup()

        if (!hasPreviousValue) backupSortState()

        _isSortVisible.update { true }
    }

    private fun restoreSortBackup(): Boolean {
        return _previousSortState.value
            ?.also { backup -> _sortState.update { backup } }
            .let { backup -> backup != null }
    }

    private fun backupSortState() {
        _previousSortState.update { _sortState.value }
    }

    fun onSortEvent(event: NoteSortEvent) {
        when (event) {
            is NoteSortEvent.PinnedPriority -> handleSelectPinnedPriority(pin = event.priority)
            NoteSortEvent.SortByPriorityAndTime -> handleSortByPriorityAndTime()
            NoteSortEvent.SortByTimeAndPriority -> handleSortByTimeAndPriority()
            NoteSortEvent.AcceptSort -> handleAcceptSort()
            NoteSortEvent.CloseWithoutSave -> handleCloseWithoutSave()
            NoteSortEvent.ClosePriorityDropDown -> handleClosesPriority()
            NoteSortEvent.ShowPriorityDropDown -> handleOpenPriority()
        }
    }

    private fun handleClosesPriority() {
        _sortState.update { old -> old.copy(isDropDownVisible = false) }
    }

    private fun handleOpenPriority() {
        _sortState.update { old -> old.copy(isDropDownVisible = true) }
    }

    private fun handleSortByTimeAndPriority() {
        _sortState.update { old -> old.copy(option = NoteSortDialogOption.BY_TIME_AND_PRIORITY) }
    }

    private fun handleSortByPriorityAndTime() {
        _sortState.update { old -> old.copy(option = NoteSortDialogOption.BY_PRIORITY_AND_TIME) }
    }

    private fun handleSelectPinnedPriority(pin: NotePriority?) {
        _sortState.update { old -> old.copy(pinPriority = pin, isDropDownVisible = false) }
    }

    private fun handleCloseWithoutSave() {
        _isSortVisible.update { false }
        _previousSortState
            .getAndUpdate { null }
            ?.let { backup -> _sortState.update { backup } }
    }

    private fun handleAcceptSort() {
        val strategy = _sortState.value.toSortStrategy()
        _strategy.update { strategy }
        _isSortVisible.update { false }
        backupSortState()
    }
}