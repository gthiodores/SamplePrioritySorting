package app.lucys.samplepriorityordering.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.lucys.samplepriorityordering.ui.model.Note
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListRoute(vm: NoteListViewModel = viewModel()) {
    val notes by vm.notes.collectAsState()
    val sortState by vm.sortState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Notes") },
                actions = {
                    TextButton(onClick = { vm.openSortDialog() }) { Text("Sort") }
                }
            )
        }
    ) { padding ->
        NoteListPage(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            notes = notes
        )

        sortState?.let { state ->
            NoteSortDialog(
                onEvent = vm::onSortEvent,
                state = state,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteListPage(
    modifier: Modifier = Modifier,
    notes: List<Note>
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        items(
            items = notes,
            key = { note -> note.id },
        ) { note ->
            NoteItemView(
                note = note,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun NoteItemView(
    note: Note,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Text(
            note.content,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
        Text(
            note.priority.label(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
        Text(
            "Created At: ${note.time.toLocalDateTime(TimeZone.currentSystemDefault())}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
    }
}