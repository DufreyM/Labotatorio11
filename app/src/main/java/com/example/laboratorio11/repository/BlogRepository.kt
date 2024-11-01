package com.example.laboratorio11.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BlogRepository {
    private val firestore = FirebaseFirestore.getInstance().collection("posts")
    private val storage = FirebaseStorage.getInstance().reference.child("posts")

    suspend fun savePost(userId: String, text: String, imageUri: Uri?) {
        val postId = firestore.document().id
        val postRef = firestore.document(postId)

        // Crear la referencia para la imagen en Firebase Storage
        val imageUrl = if (imageUri != null) {
            try {
                val imageRef = storage.child("$userId/$postId.jpg")
                imageRef.putFile(imageUri).await()  // Subir imagen
                imageRef.downloadUrl.await().toString()  // Obtener URL de descarga
            } catch (e: Exception) {
                // Log error en caso de fallo al subir la imagen
                e.printStackTrace()
                null
            }
        } else {
            null
        }

        // Crear y guardar el post en Firestore con la URL de imagen
        val post = BlogPost(userId = userId, text = text, imageUrl = imageUrl)
        postRef.set(post).await() // Guardar post en Firestore
    }

    fun getAllPosts(): Flow<List<BlogPost>> = callbackFlow {
        val listenerRegistration = firestore
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { it.toObject(BlogPost::class.java) } ?: emptyList()
                trySend(posts).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }
}

data class BlogPost(
    val userId: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
