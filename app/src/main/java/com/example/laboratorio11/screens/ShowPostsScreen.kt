package com.example.laboratorio11.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.laboratorio11.repository.BlogRepository
import com.example.laboratorio11.repository.BlogPost
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun ShowPostsScreen(blogRepository: BlogRepository) {
    // Estado para almacenar las publicaciones
    var posts by remember { mutableStateOf<List<BlogPost>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Obtener las publicaciones cuando la pantalla se carga
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            blogRepository.getAllPosts().collect { fetchedPosts ->
                posts = fetchedPosts
            }
        }
    }

    // Mostrar las publicaciones en una lista
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(posts) { post ->
            PostItem(post)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PostItem(post: BlogPost) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar imagen si existe
        post.imageUrl?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Post Image",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Mostrar el texto de la publicación
        Text(text = post.text)
    }
}
