package com.example.laboratorio11.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.laboratorio11.repository.BlogRepository
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(blogRepository: BlogRepository, userId: String) {
    var text by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                imageUri = uri
                Log.d("CreatePostScreen", "Selected Image URI: $uri")
            } else {
                Log.d("CreatePostScreen", "No image selected.")
                // Puedes dejar imageUri en null si no se selecciona ninguna imagen
                imageUri = null
            }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Write your post") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Select Image")
        }

        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 16.dp)
            )
        } else {
            Text("No image selected")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            blogRepository.savePost(userId, text, imageUri)
                            // Limpia el contenido después de la publicación exitosa
                            text = ""
                            imageUri = null
                        } catch (e: Exception) {
                            Log.e("CreatePostScreen", "Error posting: ${e.message}")
                            errorMessage = "Error al publicar el post. Intente nuevamente."
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    Log.d("CreatePostScreen", "Text is empty. Cannot post.")
                    errorMessage = "El texto no puede estar vacío."
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Posting..." else "Post")
        }

        // Mostrar mensaje de error si existe
        errorMessage?.let {
            Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
        }
    }
}
