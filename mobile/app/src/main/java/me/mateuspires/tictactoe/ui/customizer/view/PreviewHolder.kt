package me.mateuspires.tictactoe.ui.customizer.view

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import com.bumptech.glide.RequestBuilder
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.game.Player
import me.mateuspires.tictactoe.util.loadAnimation

class PreviewHolder(
        player: Player,
        private val notAttachedTextView: View,
        private val attachTextView: View,
        private val previewImage: ImageButton,
        listener: PreviewHolderActionsListener
) {

    private var hasImage: Boolean = false

    init {
        attachTextView.setOnClickListener {
            listener.onImageAttach(player)
        }

        previewImage.setOnClickListener {
            listener.onImageDetach(player)
        }
    }

    fun attach(imageLoader: RequestBuilder<Drawable>) {
        hasImage = true
        imageLoader.into(previewImage)
        setTextVisibility(false, false)
    }

    fun detach() {
        hasImage = false
        previewImage.loadAnimation(R.anim.fade_out, {}) { previewImage.setImageDrawable(null) }
        setTextVisibility(true, false)
    }

    fun setAttachAvailable(available: Boolean) {
        when {
            // If available, show attach available text
            available -> setTextVisibility(false, true)
            // If has no image, show no image attached text
            !hasImage -> setTextVisibility(true, false)
            // Otherwise the show image only
            else -> setTextVisibility(false, false)
        }
    }

    private fun setTextVisibility(notAttached: Boolean, attach: Boolean) {
        setTextVisibility(notAttachedTextView, notAttached)
        setTextVisibility(attachTextView, attach)
    }

    private fun setTextVisibility(view: View, visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.INVISIBLE

        if (view.visibility != visibility) {
            if (visible) {
                view.visibility = View.VISIBLE
                view.loadAnimation(R.anim.fade_in)
            } else {
                view.loadAnimation(R.anim.fade_out, {}, { view.visibility = View.INVISIBLE })
            }
        }
    }

    interface PreviewHolderActionsListener {

        fun onImageAttach(player: Player)

        fun onImageDetach(player: Player)
    }
}
