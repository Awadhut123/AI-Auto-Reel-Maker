
package com.autoreel.ai

import android.content.Context
import android.net.Uri
import java.io.File

import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer

@UnstableApi
object VideoEditor {

    fun createEditedMediaItem(
        videoUri: Uri,
        startTimeMs: Long = 0L,
        endTimeMs: Long? = null,
        quality: VideoExportQuality = VideoExportQuality.HD_720P,
        videoEffects: List<Effect> = VideoEffects.createEffects()
    ): EditedMediaItem {

        require(startTimeMs >= 0L) {
            "Start time cannot be negative"
        }

        if (endTimeMs != null) {
            require(endTimeMs > startTimeMs) {
                "End time must be greater than start time"
            }
        }

        val mediaItemBuilder = MediaItem.Builder()
            .setUri(videoUri)

        if (endTimeMs != null) {
            mediaItemBuilder.setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startTimeMs)
                    .setEndPositionMs(endTimeMs)
                    .build()
            )
        } else if (startTimeMs > 0L) {
            mediaItemBuilder.setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startTimeMs)
                    .build()
            )
        }

        val mediaItem = mediaItemBuilder.build()

        val allVideoEffects = mutableListOf<Effect>()

        allVideoEffects.addAll(videoEffects)

        allVideoEffects.add(
            VideoExportSettings.createResolutionEffect(quality)
        )

        val effects = Effects(
            emptyList(),
            allVideoEffects
        )

        return EditedMediaItem.Builder(mediaItem)
            .setEffects(effects)
            .build()
    }

    fun exportVideo(
        context: Context,
        editedMediaItem: EditedMediaItem,
        outputFile: File,
        onCompleted: (File) -> Unit,
        onExportError: (String) -> Unit
    ) {

        if (outputFile.exists()) {
            outputFile.delete()
        }

        val listener = object : Transformer.Listener {

            override fun onCompleted(
                composition: Composition,
                exportResult: ExportResult
            ) {
                onCompleted(outputFile)
            }

            override fun onError(
                composition: Composition,
                exportResult: ExportResult,
                exportException: ExportException
            ) {
                outputFile.delete()

                onExportError(
                    exportException.message
                        ?: "Video export failed"
                )
            }
        }

        try {
            val transformer = Transformer.Builder(
                context.applicationContext
            )
                .setVideoMimeType(MimeTypes.VIDEO_H264)
                .setAudioMimeType(MimeTypes.AUDIO_AAC)
                .addListener(listener)
                .build()

            transformer.start(
                editedMediaItem,
                outputFile.absolutePath
            )

        } catch (e: Exception) {
            outputFile.delete()

            onExportError(
                e.message ?: "Unable to start video export"
            )
        }
    }
}
