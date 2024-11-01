package com.example.laboratorio11

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home-screen", "Save Data", Icons.Filled.AddCircle)
    data object Profile : Screen("profile-screen", "Show Data", Icons.Filled.Person)
    data object CreatePost : Screen("create-post", "Create Post", Icons.Filled.Create)
    data object ShowPosts : Screen("show-posts", "Blog", Icons.AutoMirrored.Filled.List)
}
