package com.example.ivr_audio_app.android

import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivr_call_app_v3.android.Constants.Constants
import com.mayank.ivr_voice_app_v3.android.FunctionalComponents.BluetoothRepository
import com.mayank.ivr_voice_app_v3.android.UI.Components.playAudio


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun UI() {

    var power by remember {
        mutableStateOf("OFF")
    }

    var isChecked by remember {
        mutableStateOf(false)
    }

    var isaudiorunning = remember {
        mutableStateOf(false)
    }

    var mediaPlayer = remember {
        mutableStateOf<MediaPlayer?>(null)
    }

    var context = LocalContext.current

    val message by BluetoothRepository.myBluetooth.receivedData.collectAsState()
//    var context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Transparent), contentAlignment = Alignment.Center)
    {


        Column(modifier = Modifier.wrapContentSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
            Text(text = "Voice App", fontSize = 50.sp, fontWeight = FontWeight.ExtraBold,color = Color.Black)
            Spacer(modifier = Modifier.height(40.dp))

            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier.scale(1.5f),
                thumbContent = {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "",
                            modifier = Modifier
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.RadioButtonUnchecked,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(Constants.dark),
                    checkedBorderColor = Color(Constants.dark),
                    checkedTrackColor = Color(Constants.light),
                    checkedIconColor = Color(Constants.light),
                )
            )

                    Text(text = power, fontSize = 40.sp,modifier = Modifier.padding(20.dp), color = Color.Black)



        }

    }

    LaunchedEffect(key1 = message)
    {
        if(message != null){
            when (message!!.get("event").toString()) {

                "toggle_on" -> {isChecked = true}
                "toggle_off" -> {isChecked = false}
                "start" -> {
                    if (!isaudiorunning.value)
                    {
                        // message!!.get("category").toString()
                        // message!!.get("language").toString()
                        playAudio(context,message!!.get("category").toString(),message!!.get("language").toString() ,isaudiorunning , mediaPlayer)
                        BluetoothRepository.myBluetooth.resetmessage()
                    }
                }
                "stop" -> {
                    if (isaudiorunning.value )
                    {
                        if(mediaPlayer.value != null)
                        {
                            mediaPlayer.value?.stop()
                            mediaPlayer.value?.release()
                            mediaPlayer.value = null
                            isaudiorunning.value = false
                        }

                    }
                    BluetoothRepository.myBluetooth.resetmessage()
                }

            }
        }
    }



}


