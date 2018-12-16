package me.mateuspires.tictactoe.ui.customizer.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_board_cell.view.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract
import me.mateuspires.tictactoe.util.loadAnimation

class ImagesAdapter(
        private val context: Context,
        private val listener: OnImageSelectListener
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(), CustomizerContract.ImageSearchAdapter {

    private var images: Array<ImageSearch.Item> = emptyArray()
    private var selected: ViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_image,
                parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(images[position].url).apply(RequestOptions().centerCrop())
                .into(holder.view.iv_content)

        // Toggle image selection on click
        holder.view.setOnClickListener {
            if (selected == holder) {
                holder.setSelected(false)
                selected = null

                listener.onImageUnselect()
            } else {
                selected?.setSelected(false)
                selected = holder
                holder.setSelected(true)

                listener.onImageSelect(images[position])
            }
        }
    }

    /**
     * Updates the images.
     * @param images The images.
     */
    override fun update(images: Array<ImageSearch.Item>) {
        this.images = images
        selected = null
        notifyDataSetChanged()
    }

    /**
     * Unselect the selected image, if any, and animate it.
     */
    override fun unselect() {
        selected = null
        selected?.setSelected(false)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        /**
         * Sets the view as selected and apply the correspondingly animation.
         */
        fun setSelected(selected: Boolean) {
            view.loadAnimation(if (selected) R.anim.image_card_scale_up else R.anim.image_card_scale_down)
        }
    }

    interface OnImageSelectListener {

        /**
         * Notifies a image was selected.
         */
        fun onImageSelect(image: ImageSearch.Item)

        /**
         * Notifies the selected image was unselected.
         */
        fun onImageUnselect()
    }
}
