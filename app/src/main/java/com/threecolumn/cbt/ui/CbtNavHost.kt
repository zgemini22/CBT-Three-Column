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
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.threecolumn.cbt.R
import com.threecolumn.cbt.ui.about.AboutScreen
import com.threecolumn.cbt.ui.journal.JournalEntryScreen
import com.threecolumn.cbt.ui.journal.JournalListScreen
import com.threecolumn.cbt.ui.journal.JournalViewModel
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordDetailScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordEditScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordListScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordViewModel

private object Routes {
    const val THOUGHTS = "thoughts"
    const val JOURNAL = "journal"
    const val ABOUT = "about"
    const val NEW_RECORD = "record/new"
    const val RECORD_DETAIL = "record/{id}"
    const val EDIT_RECORD = "record/{id}/edit?focusPage={focusPage}"
    const val NEW_JOURNAL_ENTRY = "journal_entry/new"
    const val EDIT_JOURNAL_ENTRY = "journal_entry/{id}"
    fun recordDetail(id: Long) = "record/$id"
    fun editRecord(id: Long, focusPage: Int) = "record/$id/edit?focusPage=$focusPage"
    fun editJournalEntry(id: Long) = "journal_entry/$id"
}

private data class TopLevelDestination(
    val route: String,
    @androidx.annotation.StringRes val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.THOUGHTS, R.string.nav_thought_records, Icons.Filled.SelfImprovement),
    TopLevelDestination(Routes.JOURNAL, R.string.nav_journal, Icons.Filled.MenuBook)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CbtNavHost(
    application: CbtApplication,
    appLockEnabled: Boolean,
    biometricAvailable: Boolean,
    onToggleAppLock: (Boolean) -> Unit
) {
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
                    title = { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { navController.navigate(Routes.ABOUT) }) {
                            Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.about_desc))
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
                        val label = stringResource(destination.labelRes)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = label) },
                            label = { Text(label) }
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
                    onOpenRecord = { id -> navController.navigate(Routes.recordDetail(id)) },
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
                AboutScreen(
                    thoughtRecordRepository = application.thoughtRecordRepository,
                    journalEntryRepository = application.journalEntryRepository,
                    appLockEnabled = appLockEnabled,
                    biometricAvailable = biometricAvailable,
                    onToggleAppLock = onToggleAppLock,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.NEW_RECORD) {
                ThoughtRecordEditScreen(
                    recordId = null,
                    viewModel = thoughtViewModel,
                    onDone = { navController.popBackStack() },
                    initialPage = 0
                )
            }
            composable(
                route = Routes.RECORD_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: return@composable
                ThoughtRecordDetailScreen(
                    recordId = id,
                    viewModel = thoughtViewModel,
                    onBack = { navController.popBackStack() },
                    onEdit = { editId, focusPage ->
                        navController.navigate(Routes.editRecord(editId, focusPage))
                    }
                )
            }
            composable(
                route = Routes.EDIT_RECORD,
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType },
                    navArgument("focusPage") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: return@composable
                val focusPage = backStackEntry.arguments?.getInt("focusPage") ?: 0
                ThoughtRecordEditScreen(
                    recordId = id,
                    viewModel = thoughtViewModel,
                    onDone = { navController.popBackStack() },
                    initialPage = focusPage
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
