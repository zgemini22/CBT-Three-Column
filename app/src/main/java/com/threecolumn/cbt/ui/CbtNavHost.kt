package com.threecolumn.cbt.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
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
import com.threecolumn.cbt.ui.hobbies.HobbyIdeaViewModel
import com.threecolumn.cbt.ui.hobbies.HobbyListScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordEditScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordListScreen
import com.threecolumn.cbt.ui.thoughts.ThoughtRecordViewModel

private object Routes {
    const val THOUGHTS = "thoughts"
    const val HOBBIES = "hobbies"
    const val ABOUT = "about"
    const val NEW_RECORD = "record/new"
    const val EDIT_RECORD = "record/{id}"
    fun editRecord(id: Long) = "record/$id"
}

private data class TopLevelDestination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.THOUGHTS, "Thought Records", Icons.Filled.SelfImprovement),
    TopLevelDestination(Routes.HOBBIES, "Hobbies", Icons.Filled.Spa)
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
    val hobbyViewModel: HobbyIdeaViewModel = viewModel(
        factory = HobbyIdeaViewModel.Factory(application.hobbyIdeaRepository)
    )

    val showChrome = currentRoute?.hierarchy?.any { it.route == Routes.THOUGHTS || it.route == Routes.HOBBIES } == true

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
            composable(Routes.HOBBIES) {
                HobbyListScreen(viewModel = hobbyViewModel)
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
        }
    }
}
