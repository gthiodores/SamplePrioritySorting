package app.lucys.samplepriorityordering.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.lucys.samplepriorityordering.ui.model.Note
import kotlinx.datetime.Clock
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { padding ->
        NoteListPage(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            notes = notes
        )
    }

    sortState?.let { state ->
        NoteSortDialog(
            onEvent = vm::onSortEvent,
            state = state,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteListPage(
    modifier: Modifier = Modifier,
    notes: List<Note>
) {
    Row(modifier = modifier) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.weight(6f),
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = PaddingValues(16.dp),
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
        LazyColumn(
            modifier = Modifier.weight(4f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            item { Spacer(Modifier.padding(0.dp)) }
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
}

@Composable
private fun NoteItemView(
    note: Note,
    modifier: Modifier = Modifier,
) {
    var lapse by remember { mutableStateOf(Duration.ZERO) }
    val timeLabel by rememberDerivedStateOf {
        lapse.toComponents { m, s, _ -> String.format("%02d:%02d", m, s) }
    }

    LaunchedEffect(key1 = note.id) {
        lapse = Clock.System.now().minus(note.time)
        timer(initialDelay = 100L, period = 1_000L) { lapse = lapse.plus(1.seconds) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(4.dp)),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            note.content,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
        Text(
            note.priority.label(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
        Text(
            timeLabel,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        )
    }
}

@Composable
fun <T> rememberDerivedStateOf(f: () -> T): State<T> {
    return remember { derivedStateOf(f) }
}