
package com.autoreel.ai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.result.PickVisualMediaRequest

private val DarkBackground = Color(0xFF101018)
private val CardBackground = Color(0xFF1D1D2B)
private val Purple = Color(0xFF9B6DFF)
private val LightPurple = Color(0xFFC5A8FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Purple,
                    background = DarkBackground,
                    surface = CardBackground,
                    onPrimary = Color.White,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                AutoReelHomeScreen()
            }
        }
    }
}

@Composable
fun AutoReelHomeScreen() {

    val context = LocalContext.current

    var showVideoOptions by remember { mutableStateOf(false) }
    var selectedVideoUri by remember {
        mutableStateOf<android.net.Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedVideoUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data

            if (uri != null) {
                selectedVideoUri = uri
            } else {
                selectedVideoUri = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "AutoReel AI",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Create amazing reels with AI",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "✦ AI",
                color = LightPurple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF542A9E),
                            Color(0xFF25204A),
                            Color(0xFF17172B)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
        ) {
            Column {
                Text(
                    text = "YOUR VIDEO,",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "YOUR NEXT REEL ✨",
                    color = LightPurple,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Turn ordinary videos into amazing reels.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        showVideoOptions = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "+  Create New Reel",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (selectedVideoUri != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Purple)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "✓ Video Selected",
                        color = LightPurple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your video is ready for the next step.",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            showVideoOptions = true
                        }
                    ) {
                        Text("Choose Another Video")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Text(
            text = "Choose a Style",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TemplateCard("🔥", "Trending", Modifier.weight(1f))
            TemplateCard("🎬", "Cinematic", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TemplateCard("✨", "Aesthetic", Modifier.weight(1f))
            TemplateCard("⚡", "Fast Beat", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "My Reels",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardBackground,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF343448))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎥", fontSize = 32.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "No reels created yet",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Your finished videos will appear here.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Made with AutoReel AI ✦",
            color = Color.Gray,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showVideoOptions) {
        AlertDialog(
            onDismissRequest = {
                showVideoOptions = false
            },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Create New Reel",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Choose how you want to add your video.",
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showVideoOptions = false

                            galleryLauncher.launch(
                                ActivityResultContracts.PickVisualMedia.VideoOnly
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple
                        )
                    ) {
                        Text("📁  Choose from Gallery")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            showVideoOptions = false

                            val cameraIntent = Intent(
                                MediaStore.ACTION_VIDEO_CAPTURE
                            ).apply {
                                putExtra(
                                    MediaStore.EXTRA_DURATION_LIMIT,
                                    60
                                )
                            }

                            if (cameraIntent.resolveActivity(
                                    context.packageManager
                                ) != null
                            ) {
                                cameraLauncher.launch(cameraIntent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📷  Record Video",
                            color = LightPurple
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showVideoOptions = false
                    }
                ) {
                    Text("Cancel", color = LightPurple)
                }
            }
        )
    }
}

@Composable
fun TemplateCard(
    emoji: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(105.dp),
        color = CardBackground,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF343448))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 27.sp)

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
