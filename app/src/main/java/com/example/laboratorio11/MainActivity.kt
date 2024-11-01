package com.example.laboratorio11

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.laboratorio11.data.UserDataStore
import com.example.laboratorio11.repository.BlogRepository
import com.example.laboratorio11.screens.SaveDataScreen
import com.example.laboratorio11.screens.ShowDataScreen
import com.example.laboratorio11.screens.CreatePostScreen
import com.example.laboratorio11.screens.ShowPostsScreen
import com.example.laboratorio11.ui.theme.Laboratorio11Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userDataStore = UserDataStore(this)
        val blogRepository = BlogRepository() // Inicializa BlogRepository aquí
        val userId = "user123" // Usa un valor fijo de ejemplo o recupéralo de UserDataStore

        enableEdgeToEdge()
        setContent {
            Laboratorio11Theme {
                SharedStorageApp(
                    modifier = Modifier.fillMaxSize(),
                    userDataStore = userDataStore,
                    blogRepository = blogRepository,
                    userId = userId
                )
            }
        }
    }
}

@Composable
fun SharedStorageApp(
    modifier: Modifier = Modifier,
    userDataStore: UserDataStore,
    blogRepository: BlogRepository,
    userId: String
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHostContainer(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            userDataStore = userDataStore,
            blogRepository = blogRepository,
            userId = userId
        )
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userDataStore: UserDataStore,
    blogRepository: BlogRepository,
    userId: String
) {
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            SaveDataScreen(userDataStore = userDataStore, userId = userId) // Pass userId here
        }
        composable(Screen.Profile.route) {
            ShowDataScreen(userDataStore = userDataStore)
        }
        composable(Screen.CreatePost.route) {
            CreatePostScreen(blogRepository = blogRepository, userId = userId)
        }
        composable(Screen.ShowPosts.route) {
            ShowPostsScreen(blogRepository = blogRepository)
        }
    }
}



@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.CreatePost,
        Screen.ShowPosts
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
