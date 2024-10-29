package com.example.ivr_call_app_v3.android.UI.Navigation

import BluetoothViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ivr_audio_app_20.android.Audio_Screen
import com.mayank.ivr_voice_app_v3.android.FunctionalComponents.BluetoothRepository
import com.mayank.ivr_voice_app_v3.android.UI.Splash

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyNavhostController()
{
    val navHostController = rememberNavController()
    var context = LocalContext.current
    BluetoothRepository.myBluetooth = BluetoothViewModel(context)



    NavHost(navController = navHostController, startDestination = "splash" )
    {
        composable("splash")
        {
            Splash(navHostController)
//                Message()
        }
//
        composable("audio")
        {
           Audio_Screen()
        }
//
//        composable("manualsetup")
//        {
//            var type = 1
//            Setup(navHostController, viewModel, type)
//        }
//
//        composable("bleconnect")
//        {
//            BLEconnect(navHostController,viewModel)
//        }
    }
}