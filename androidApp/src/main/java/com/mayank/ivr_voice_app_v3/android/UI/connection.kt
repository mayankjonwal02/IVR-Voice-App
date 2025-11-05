package com.example.ivr_audio_app_20.android

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ivr_call_app_v3.android.Constants.Constants
import com.mayank.ivr_voice_app_v3.android.FunctionalComponents.BluetoothRepository
import com.mayank.ivr_voice_app_v3.android.UI.Components.playAudio

import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun connection()
{
    var context = LocalContext.current
    val status by BluetoothRepository.myBluetooth.connectionStatus.collectAsState()
    val devicelist by BluetoothRepository.myBluetooth.pairedDevices.collectAsState()

    var showpopup = remember {
        mutableStateOf(false)
    }
    var showaudiofiles = remember {
        mutableStateOf(false)
    }

    var mediaPlayer = remember {
        mutableStateOf<MediaPlayer?>(null)
    }
    
    var intentlauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult())
    {
        result ->

        if(result.resultCode == Activity.RESULT_OK)
        {
            BluetoothRepository.myBluetooth.fetchPairedDevices() // .fetchdevices()
        }
        else
        {
            Toast.makeText(context,"Bluetooth Not Enabled",Toast.LENGTH_SHORT).show()
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Transparent), contentAlignment = Alignment.TopCenter)
    {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally)
        {
            OutlinedButton(onClick = {

                                    BluetoothRepository.myBluetooth.waitForConnection()
                                     },colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(Constants.dark), contentColor = Color.Black),
                modifier = Modifier.padding(65.dp) )
            {
                Text(text = "Listen", fontSize = 20.sp)
            }

//

            Text(
                text = "Status : ${status}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )


            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent), contentAlignment = Alignment.BottomCenter) {
                Column(
//                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    OutlinedButton(
                        onClick = { showpopup.value = !showpopup.value },
                        modifier = Modifier
                            .padding(start = 20.dp, bottom = 20.dp)
                            ,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(3.dp, Color.Black)
                    ) {
                        Text(text = "Add Audio")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { showaudiofiles.value = !showaudiofiles.value },
                        modifier = Modifier
                            .padding(end = 20.dp, bottom = 20.dp)
                            ,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black,
                            containerColor = Color.Transparent
                        ),
                        border = BorderStroke(3.dp, Color.Black)
                    ) {
                        Text(text = "Show Audio Files")
                    }
                }
            }

        }
    }

    @Composable
    fun myaudiofiles(showaudiofiles: MutableState<Boolean>) {
        var filedict = mutableMapOf<String, List<File>>()
        val maindirname = "IVR_AUDIO_LOCATION"
        val maindir = File(context.getExternalFilesDir(null), maindirname)

        // Create the directory if it does not exist
        if (!maindir.exists()) {
            maindir.mkdirs()
        }

        // Check if maindir is indeed a directory
        if (maindir.isDirectory) {
            val categories = maindir.listFiles()?.map { it.name } ?: emptyList()

            categories.forEach { category ->
                val subdir = File(maindir, category)

                // Ensure subdir exists before listing files
                if (subdir.isDirectory) {
                    val languagelist = subdir.listFiles()?.map {
                        it
//                    Uri.fromFile(it)
                    } ?: emptyList()
                    filedict[category] = languagelist
                }
            }
        } else {
            // Handle the case where maindir is not a directory
            Log.e("myaudiofiles", "maindir is not a directory")
        }

        // Rest of your Dialog and LazyColumn code here...
        Dialog(onDismissRequest = { showaudiofiles.value = false }) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(3.dp, Color.Magenta)
            ) {
                LazyColumn(contentPadding = PaddingValues(all = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filedict.forEach { (key, value) ->
                        stickyHeader {
                            Text(
                                text = key,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Magenta)
                                    .padding(12.dp),
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                        items(value) { language ->
                            Text(
                                text = language.name.toString(),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .background(Color.Magenta.copy(alpha = 0.1f))
                                    .padding(12.dp)
                                    .clickable {

                                    },
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun mydialog(showpopup: MutableState<Boolean>)
    {

        Dialog(onDismissRequest = { showpopup.value = !showpopup.value },
        properties = DialogProperties(dismissOnBackPress = true , dismissOnClickOutside = true)
        ) {

            var audio by remember {
                mutableStateOf("No Audio")
            }
            var selectedfileuri : Uri? = null
            var category by remember {
                mutableStateOf("")
            }
            var language by remember {
                mutableStateOf("")
            }

            var pickaudiolauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult())
            {
                    result ->
                if(result.resultCode == Activity.RESULT_OK)
                {
                    val selectedAudioUri : Uri? = result.data?.data
                    Toast.makeText(context,selectedAudioUri.toString(),Toast.LENGTH_SHORT).show()
                    audio = selectedAudioUri.toString()
                    if (selectedAudioUri != null) {
                        selectedfileuri = selectedAudioUri
                    }



                }
            }
            fun pickaudio()
            {
                val intent = Intent(Intent.ACTION_GET_CONTENT)  //,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                intent.type = "*/*"
                pickaudiolauncher.launch(intent)
            }
            fun setdir(category: String, language: String, selectedfileuri: Uri)
            {
                var maindirname = "IVR_AUDIO_LOCATION"
                var subdirname = category.toUpperCase()
                var audiofilename = "${language.toUpperCase()}.mp3"
                var maindir = File(context.getExternalFilesDir(null),maindirname)
                var sourceinputstream = context.contentResolver.openInputStream(selectedfileuri)
                if(!maindir.exists())
                {
                    maindir.mkdir()
                }
                var subdir = File(maindir,subdirname)
                if(!subdir.exists())
                {
                   subdir.mkdir()
                }
                var audiofile = File(subdir,audiofilename)
                if(audiofile.exists())
                {
                    audiofile.delete()

                }
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Main)
                    {
                        sourceinputstream?.use {
                                input ->
                            FileOutputStream(audiofile).use {
                                    output ->
                                input.copyTo(output)
                            }
                        }
                        Toast.makeText(context,"$category/$language.mp3 saved",Toast.LENGTH_SHORT).show()
                        showpopup.value = !showpopup.value
                    }
                }


            }

            Card(modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent)
                .shadow(elevation = 0.dp, shape = RoundedCornerShape(20.dp)), shape = RoundedCornerShape(20.dp),
//                backgroundColor = Color.White,
                border = BorderStroke(2.dp, color = Color.Magenta)
            )
            {
                Column(modifier = Modifier
                    .padding(20.dp)
                    .background(Color.Transparent)
                    .verticalScroll(rememberScrollState())) {
                    Text(text = "Add Audio File", color = Color.Black, fontFamily = FontFamily.Default, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, fontStyle = FontStyle.Normal)
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(value = category , placeholder = { Text(text = "Enter Category")}, label = { Text(
                        text = "Category"
                    )}, onValueChange = {category = it}, colors = TextFieldDefaults.outlinedTextFieldColors(focusedTextColor =  Color.Black, cursorColor = Color.Black, containerColor = Color.White, focusedPlaceholderColor =  Color.LightGray, focusedLabelColor = Color.Magenta, focusedBorderColor = Color.Magenta, unfocusedLabelColor = Color.LightGray, unfocusedBorderColor = Color.LightGray))
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(value = language , placeholder = { Text(text = "Enter Language")}, label = { Text(
                        text = "Language"
                    )}, onValueChange = {language = it}, colors = TextFieldDefaults.outlinedTextFieldColors(focusedTextColor = Color.Black, cursorColor = Color.Black, containerColor = Color.White, focusedPlaceholderColor = Color.LightGray, focusedLabelColor = Color.Magenta, focusedBorderColor = Color.Magenta, unfocusedLabelColor = Color.LightGray, unfocusedBorderColor = Color.LightGray))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = "file : $audio")
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween){
                        OutlinedButton(
                            onClick = { pickaudio() },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Magenta
                            ),
                            border = BorderStroke(
                                2.dp,
                                Color.Magenta
                            )
                        ) {
                            Text(text = "Choose File")
                        }
                        if (audio != "No Audio") {
                            Spacer(modifier = Modifier.width(20.dp))
                            OutlinedButton(
                                onClick = {
                                          if(language.isNotBlank() || category.isNotBlank() )
                                          {
                                              selectedfileuri?.let { setdir(category,language, it) }
                                          }
                                    else
                                          {
                                              Toast.makeText(context,"Fields Empty",Toast.LENGTH_SHORT).show()
                                          }
                                          },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.Magenta
                                ),
                                border = BorderStroke(
                                    2.dp,
                                    Color.Magenta
                                )
                            ) {
                                Text(text = "Save File")
                            }
                        }


                    }



                }
            }
        }
    }




    if(showpopup.value)
    {
        mydialog(showpopup)
    }

    if(showaudiofiles.value)
    {
        myaudiofiles(showaudiofiles)
    }

}


fun fetchdevices(
    context: Context,
    intentlauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
)
{
    fun handleintent()
    {
        var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        intentlauncher.launch(intent)
    }

//    if(mybluetooth.bluetoothadapter.isEnabled)
//    {
//        mybluetooth.fetchdevices()
//    }
//    else
//    {
//        handleintent()
//    }

//    handleintent()
}