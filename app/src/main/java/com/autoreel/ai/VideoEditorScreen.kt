
package com.autoreel.ai

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import java.io.File

@OptIn(UnstableApi::class)
@Composable
fun VideoEditorScreen(
    videoFile: File,
    onExportCompleted: (File) -> Unit
) {
    val context = LocalContext.current

    val durationMs = remember(videoFile) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoFile.absolutePath)

            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L

            retriever.release()
            duration
        } catch (e: Exception) {
            0L
        }
    }

    if (durationMs <= 0L) {
        Text("Unable to read video duration.")
        return
    }

    val durationSeconds = durationMs / 1000f
    val canTrim = durationMs >= 2000L

    var startSeconds by remember(videoFile) {
        mutableFloatStateOf(0f)
    }

    var endSeconds by remember(videoFile) {
        mutableFloatStateOf(durationSeconds)
    }

    var brightness by remember {
        mutableFloatStateOf(0f)
    }

    var contrast by remember {
        mutableFloatStateOf(0f)
    }

    var saturation by remember {
        mutableFloatStateOf(0f)
    }

    var backgroundBlur by remember {
        mutableStateOf(false)
    }

    var selectedQuality by remember {
        mutableStateOf(VideoExportQuality.HD_720P)
    }

    var isExporting by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Video Editor",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Video Duration: ${durationSeconds.toInt()} seconds")

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Video Trimming",
            style = MaterialTheme.typography.titleMedium
        )

        if (canTrim) {

            Text("Start: ${startSeconds.toInt()} sec")

            Slider(
                value = startSeconds,
                onValueChange = {
                    startSeconds = it.coerceAtMost(
                        endSeconds - 1f
                    )
                },
                valueRange = 0f..(durationSeconds - 1f)
            )

            Text("End: ${endSeconds.toInt()} sec")

            Slider(
                value = endSeconds,
                onValueChange = {
                    endSeconds = it.coerceAtLeast(
                        startSeconds + 1f
                    )
                },
                valueRange = (startSeconds + 1f)..durationSeconds
            )

        } else {
            Text("Video is too short for trimming controls.")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Export Quality",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Button(
                onClick = {
                    selectedQuality = VideoExportQuality.HD_720P
                },
                enabled = !isExporting
            ) {
                Text("720p")
            }

            Button(
                onClick = {
                    selectedQuality = VideoExportQuality.FULL_HD_1080P
                },
                enabled = !isExporting
            ) {
                Text("1080p")
            }
        }

        Text("Selected: ${selectedQuality.displayName}")

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Color Correction",
            style = MaterialTheme.typography.titleMedium
        )

        Text("Brightness: ${"%.2f".format(brightness)}")

        Slider(
            value = brightness,
            onValueChange = { brightness = it },
            valueRange = -1f..1f,
            enabled = !isExporting
        )

        Text("Contrast: ${"%.2f".format(contrast)}")

        Slider(
            value = contrast,
            onValueChange = { contrast = it },
            valueRange = -1f..1f,
            enabled = !isExporting
        )

        Text("Saturation: ${saturation.toInt()}")

        Slider(
            value = saturation,
            onValueChange = { saturation = it },
            valueRange = -100f..100f,
            enabled = !isExporting
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Background Effect",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {

            Checkbox(
                checked = backgroundBlur,
                onCheckedChange = {
                    backgroundBlur = it
                },
                enabled = !isExporting
            )

            Text("Portrait Background Blur")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = {

                try {

                    isExporting = true

                    val effects = mutableListOf<Effect>()

                    effects.addAll(
                        VideoEffects.createColorEffects(
                            brightness = brightness,
                            contrast = contrast,
                            saturation = saturation
                        )
                    )

                    if (backgroundBlur) {
                        effects.add(
                            VideoBackgroundEffects
                                .createPortraitBlurBackground()
                        )
                    }

                    val startMs = (
                        startSeconds * 1000
                    ).toLong()

                    val endMs = if (
                        endSeconds >= durationSeconds - 0.01f
                    ) {
                        durationMs
                    } else {
                        (endSeconds * 1000).toLong()
                    }

                    val editedItem =
                        VideoEditor.createEditedMediaItem(
                            videoUri = Uri.fromFile(videoFile),
                            startTimeMs = startMs,
                            endTimeMs = endMs,
                            quality = selectedQuality,
                            videoEffects = effects
                        )

                    val outputFile = File(
                        context.cacheDir,
                        "autoreel_export_${System.currentTimeMillis()}.mp4"
                    )

                    VideoEditor.exportVideo(
                        context = context,
                        editedMediaItem = editedItem,
                        outputFile = outputFile,
                        onCompleted = { file ->

                            isExporting = false

                            Toast.makeText(
                                context,
                                "Video export completed!",
                                Toast.LENGTH_LONG
                            ).show()

                            onExportCompleted(file)
                        },
                        onExportError = { error ->

                            isExporting = false

                            Toast.makeText(
                                context,
                                "Export failed: $error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )

                } catch (e: Exception) {

                    isExporting = false

                    Toast.makeText(
                        context,
                        e.message ?: "Unable to export video",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isExporting
        ) {
            Text(
                if (isExporting) {
                    "Exporting Video..."
                } else {
                    "Create Reel"
                }
            )
        }

        if (isExporting) {

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )

            Text("Please wait while your video is exporting.")
        }
    }
}
