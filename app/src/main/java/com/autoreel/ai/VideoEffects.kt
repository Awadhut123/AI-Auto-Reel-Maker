
package com.autoreel.ai

import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Brightness
import androidx.media3.effect.Contrast
import androidx.media3.effect.HslAdjustment
import androidx.media3.effect.Presentation

@UnstableApi
object VideoEffects {

    fun createVerticalReelEffect(): Effect {
        return Presentation.createForAspectRatio(
            9f / 16f,
            Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP
        )
    }

    fun createColorEffects(
        brightness: Float = 0f,
        contrast: Float = 0f,
        saturation: Float = 0f
    ): List<Effect> {

        val effects = mutableListOf<Effect>()

        if (brightness != 0f) {
            effects.add(
                Brightness(
                    brightness.coerceIn(-1f, 1f)
                )
            )
        }

        if (contrast != 0f) {
            effects.add(
                Contrast(
                    contrast.coerceIn(-1f, 1f)
                )
            )
        }

        if (saturation != 0f) {
            effects.add(
                HslAdjustment.Builder()
                    .adjustSaturation(
                        saturation.coerceIn(-100f, 100f)
                    )
                    .build()
            )
        }

        return effects
    }

    fun createEffects(
        verticalReel: Boolean = true,
        brightness: Float = 0f,
        contrast: Float = 0f,
        saturation: Float = 0f
    ): List<Effect> {

        val effects = mutableListOf<Effect>()

        if (verticalReel) {
            effects.add(createVerticalReelEffect())
        }

        effects.addAll(
            createColorEffects(
                brightness,
                contrast,
                saturation
            )
        )

        return effects
    }
}
