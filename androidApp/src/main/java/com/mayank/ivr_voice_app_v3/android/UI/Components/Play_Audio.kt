package com.mayank.ivr_voice_app_v3.android.UI.Components
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.core.net.toUri
import com.mayank.ivr_voice_app_v3.android.FunctionalComponents.BluetoothRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

fun playAudio(
    context: Context,
    category: String,
    language: String,
    isaudiorunning: MutableState<Boolean>,
    mediaPlayer: MutableState<MediaPlayer?>,
) {
    val maindirname = "IVR_AUDIO_LOCATION"
    val maindir = File(context.getExternalFilesDir(null), maindirname)

    CoroutineScope(Dispatchers.IO).launch {
        if (!maindir.exists()) {
            Log.e("Audio", "MainDIR doesn't exist")
            sendStopResponse("MainDIR doesn't exist", isaudiorunning)
            return@launch
        }

        val categoryDir = File(maindir, category)
        if (!categoryDir.exists()) {
            Log.e("Audio", "category doesn't exist")
            sendStopResponse("Category doesn't exist", isaudiorunning)
            return@launch
        }

        val languageAudioFile = File(categoryDir, "$language.mp3")
        if (!languageAudioFile.exists()) {
            Log.e("Audio", "Language doesn't exist")
            sendStopResponse("Language doesn't exist", isaudiorunning)
            return@launch
        }

        val fileUri = languageAudioFile.toUri()

        // Create and prepare the MediaPlayer
        mediaPlayer.value = MediaPlayer().apply {
            setDataSource(context, fileUri)
            prepare() // Prepare synchronously
            start() // Start playing
            isaudiorunning.value = true

            setOnCompletionListener {

                releaseMediaPlayer(mediaPlayer)
                sendStopResponse("Perfectly Responded", isaudiorunning)
            }
        }

        // Optional: Show a toast message or any UI feedback
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Playing audio...", Toast.LENGTH_SHORT).show()
        }
    }
}

// Function to send stop response via Bluetooth
private fun sendStopResponse(response: String, isaudiorunning: MutableState<Boolean>) {
    isaudiorunning.value = false
    val message = hashMapOf<String, String>(
        "event" to "stop",
        "responce" to response
    )
    BluetoothRepository.myBluetooth.sendData(message)
}

// Function to release MediaPlayer resources
private fun releaseMediaPlayer(mediaPlayer: MutableState<MediaPlayer?>) {
    mediaPlayer.value?.release()
    mediaPlayer.value = null // Set to null to avoid memory leaks
}
