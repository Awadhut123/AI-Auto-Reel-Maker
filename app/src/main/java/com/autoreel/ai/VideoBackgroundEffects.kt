
package com.autoreel.ai

import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.GaussianBlur
import androidx.media3.effect.GaussianBlurWithFrameOverlaid

@UnstableApi
object VideoBackgroundEffects {

    /**
     * Creates a blurred background with the original
     * video displayed sharply in the centre.
     *
     * Default scale is intended for a 16:9 landscape video
     * with a portrait-style centre frame.
     */
    fun createPortraitBlurBackground(
        blurStrength: Float = 20f,
        sharpFrameScaleX: Float = 0.32f,
        sharpFrameScaleY: Float = 1f
    ): Effect {

        require(blurStrength > 0f) {
            "Blur strength must be greater than zero"
        }

        require(sharpFrameScaleX > 0f && sharpFrameScaleX <= 1f) {
            "Invalid horizontal frame scale"
        }

        require(sharpFrameScaleY > 0f && sharpFrameScaleY <= 1f) {
            "Invalid vertical frame scale"
        }

        return GaussianBlurWithFrameOverlaid(
            blurStrength,
            sharpFrameScaleX,
            sharpFrameScaleY
        )
    }

    /**
     * Creates a full-frame blur effect.
     */
    fun createFullVideoBlur(
        blurStrength: Float = 12f
    ): Effect {

        require(blurStrength > 0f) {
            "Blur strength must be greater than zero"
        }

        return GaussianBlur(blurStrength)
    }
}
