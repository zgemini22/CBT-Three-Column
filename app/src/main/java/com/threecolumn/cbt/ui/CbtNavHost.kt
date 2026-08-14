package com.threecolumn.cbt.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.threecolumn.cbt.CbtApplication
import com.threecolumn.cbt.ui.about.AboutScreen
import com.threecolumn.cbt.ui.journal.JournalEntryScreen
import com.threecolumn.cbt.ui.journal.JournalListScreen
import com.threecolumn.cbt.ui.journal.JournalViewModel
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordEditScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordListScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordViewModel

private object Routes {
    const val THOUGHTS = "thoughts"
    const val JOURNAL = "journal"
    const val ABOUT = "about"
    const val NEW_RECORD = "record/new"
    const val EDIT_RECORD = "record/{id}"
    const val NEW_JOURNAL_ENTRY = "journal_entry/new"
    const val EDIT_JOURNAL_ENTRY = "journal_entry/{id}"
    fun editRecord(id: Long) = "record/$id"
    fun editJournalEntry(id: Long) = "journal_entry/$id"
}

private data class TopLevelDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.THOUGHTS, "Thought Records", Icons.Filled.SelfImprovement),
    TopLevelDestination(Routes.JOURNAL, "Journal", Icons.Filled.MenuBook)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CbtNavHost(application: CbtApplication) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    val thoughtViewModel: ThoughtRecordViewModel = viewModel(
        factory = ThoughtRecordViewModel.Factory(application.thoughtRecordRepository)
    )
    val journalViewModel: JournalViewModel = viewModel(
        factory = JournalViewModel.Factory(application.journalEntryRepository)
    )

    val showChrome = currentRoute?.hierarchy?.any { it.route == Routes.THOUGHTS || it.route == Routes.JOURNAL } == true

    Scaffold(
        topBar = {
            if (showChrome) {
                TopAppBar(
                    title = { Text("Three Column CBT") },
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.ABOUT) }) {
                            Icon(Icons.Filled.Info, contentDescription = "About")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentRoute?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.THOUGHTS,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.THOUGHTS) {
                ThoughtRecordListScreen(
                    viewModel = thoughtViewModel,
                    onOpenRecord = { id -> navController.navigate(Routes.editRecord(id)) },
                    onNewRecord = { navController.navigate(Routes.NEW_RECORD) }
                )
            }
            composable(Routes.JOURNAL) {
                JournalListScreen(
                    viewModel = journalViewModel,
                    onOpenEntry = { id -> navController.navigate(Routes.editJournalEntry(id)) },
                    onNewEntry = { navController.navigate(Routes.NEW_JOURNAL_ENTRY) }
                )
            }
            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.NEW_RECORD) {
                ThoughtRecordEditScreen(
                    recordId = null,
                    viewModel = thoughtViewModel,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.EDIT_RECORD,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: return@composable
                ThoughtRecordEditScreen(
                    recordId = id,
                    viewModel = thoughtViewModel,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(Routes.NEW_JOURNAL_ENTRY) {
                JournalEntryScreen(
                    entryId = null,
                    viewModel = journalViewModel,
                    onDone = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.EDIT_JOURNAL_ENTRY,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: return@composable
                JournalEntryScreen(
                    entryId = id,
                    viewModel = journalViewModel,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}
