
package com.autoreel.ai

import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation

enum class VideoExportQuality(
    val width: Int,
    val height: Int,
    val displayName: String
) {
    HD_720P(
        width = 720,
        height = 1280,
        displayName = "720p HD"
    ),

    FULL_HD_1080P(
        width = 1080,
        height = 1920,
        displayName = "1080p Full HD"
    )
}

@UnstableApi
object VideoExportSettings {

    fun createResolutionEffect(
        quality: VideoExportQuality
    ): Effect {

        return Presentation.createForWidthAndHeight(
            quality.width,
            quality.height,
            Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP
        )
    }

    fun getAvailableQualities(): List<VideoExportQuality> {
        return VideoExportQuality.values().toList()
    }
}
