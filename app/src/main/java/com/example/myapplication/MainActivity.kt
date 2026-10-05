package com.example.myapplication

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PermissionScreen()
            }
        }
    }
}

@Composable
fun PermissionScreen() {
    var photo by remember { mutableStateOf<Bitmap?>(null) }
    var status by remember { mutableStateOf("Bir düyməyə bas") }


    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        photo = bitmap
        status = if (bitmap != null) "Şəkil çəkildi" else "Ləğv edildi"
    }

    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) cameraLauncher.launch(null)
        else status = "Camera icazəsi verilmədi"
    }


    val locationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        status = if (result.values.any { it }) "Location icazəsi verildi ✅"
        else "Location icazəsi verilmədi"
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        status = if (uri != null) "Şəkil seçildi: $uri" else "Heç nə seçilmədi"
    }

    val galleryPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) galleryLauncher.launch("image/*")
        else status = "Gallery icazəsi verilmədi"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = status, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(24.dp))

        photo?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Spacer(Modifier.height(24.dp))
        }

        Button(
            onClick = { cameraPermission.launch(Manifest.permission.CAMERA) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Camera") }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                locationPermission.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Location") }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val perm = if (Build.VERSION.SDK_INT >= 33)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
                galleryPermission.launch(perm)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Gallery") }
    }
}