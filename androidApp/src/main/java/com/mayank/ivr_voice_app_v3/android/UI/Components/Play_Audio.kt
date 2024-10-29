package com.mayank.ivr_voice_app_v3.android.UI.Components

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//@Composable
fun playAudio(uri: Uri, context: Context) {
    var mediaPlayer: MediaPlayer? = null

    // Ensure the MediaPlayer is initialized
    CoroutineScope(Dispatchers.IO).launch {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri) // Set the data source to the URI
            prepare() // Prepare the media player asynchronously
            start() // Start playing
        }

        // Release the MediaPlayer when done or if the composable leaves the composition

    }

    // Optional: Show a toast message or any UI feedback
    Toast.makeText(context, "Playing audio...", Toast.LENGTH_SHORT).show()
}
