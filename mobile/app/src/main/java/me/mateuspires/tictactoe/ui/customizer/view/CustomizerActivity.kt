package me.mateuspires.tictactoe.ui.customizer.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_customizer.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.data.network.ImageSearchApiService
import me.mateuspires.tictactoe.data.persistence.PlayersImagesRepository
import me.mateuspires.tictactoe.game.Player
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract
import me.mateuspires.tictactoe.ui.customizer.presenter.CustomizerPresenter
import me.mateuspires.tictactoe.util.loadAnimation
import java.util.concurrent.TimeUnit


class CustomizerActivity : AppCompatActivity(), CustomizerContract.View,
        ImagesAdapter.OnImageSelectListener {

    companion object {
        const val TAG = "TTT.CustomizerActivity"
    }

    private var presenter: CustomizerContract.Presenter? = null
    private val imagesAdapter = ImagesAdapter(this,  this)
    private var loading = false
    private var xHolder: PreviewHolder? = null
    private var yHolder: PreviewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customizer)

        presenter = CustomizerPresenter(this,
                PlayersImagesRepository(this), ImageSearchApiService.load())

        // Subscribe for the search results
        presenter?.getSearchResultsObservable()?.subscribe(
                { updateImages(it.items) }, { Log.e(TAG, it.message) })

        // Sets the listener for the search field with debounce behavior
        val searchFieldSubject: PublishSubject<String> = PublishSubject.create()
        searchFieldSubject.debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { search(it) }

        // Calls the listener above on text change
        et_search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFieldSubject.onNext(s.toString())
            }
        })

        // Sets the recycler view
        rv_images.adapter = imagesAdapter
        rv_images.layoutManager = GridLayoutManager(this, 4)
        (rv_images.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        // Done button behaviour
        bt_done.setOnClickListener {
            presenter?.done()
            finish()
        }

        // Sets the players holders
        xHolder = PreviewHolder(Player.X, tv_x_not_selected, tv_x_attach, ib_x_preview,
                presenter as PreviewHolder.PreviewHolderActionsListener)
        yHolder = PreviewHolder(Player.Y, tv_y_not_selected, tv_y_attach, ib_y_preview,
                presenter as PreviewHolder.PreviewHolderActionsListener)

        // Sets the players images to the holders
        presenter?.getXImageItem()?.let{ attachImage(Player.X, it) }
        presenter?.getYImageItem()?.let{ attachImage(Player.Y, it) }
    }

    override fun onImageSelect(image: ImageSearch.Item) {
        Log.d(TAG, "Image select ${image.url}")
        presenter?.selectImage(image)
        setAttachAvailable(true)
    }

    override fun onImageUnselect() {
        Log.d(TAG, "Image unselect")
        presenter?.unselect()
        setAttachAvailable(false)
    }

    override fun onImageAttach(player: Player, image: ImageSearch.Item) {
        Log.d(TAG, "Image attach ${player.name} ${image.url}")

        imagesAdapter.unselect()
        setAttachAvailable(false)

        val loader: RequestBuilder<Drawable> = Glide.with(this).load(image.url)
                .apply(RequestOptions().centerCrop())

        when (player) {
            Player.X -> xHolder?.attach(loader)
            Player.Y -> yHolder?.attach(loader)
        }
    }

    override fun onImageDetach(player: Player) {
        Log.d(TAG, "Image attach ${player.name}")

        when (player) {
            Player.X -> xHolder?.detach()
            Player.Y -> yHolder?.detach()
        }
    }

    private fun attachImage(player: Player, image: ImageSearch.Item) {
        val loader: RequestBuilder<Drawable> = Glide.with(this).load(image.url)
                .apply(RequestOptions().centerCrop())

        when (player) {
            Player.X -> xHolder?.attach(loader)
            Player.Y -> yHolder?.attach(loader)
        }
    }

    /**
     * Shows the touch to attach message on top of the images preview.
     * @param available If true, the message is shown.
     */
    private fun setAttachAvailable(available: Boolean) {
        xHolder?.setAttachAvailable(available)
        yHolder?.setAttachAvailable(available)
    }

    /**
     * Show the loading spinner and makes a search call.
     * @param query The text to search.
     */
    private fun search(query: String) {
        Log.d(TAG, "Search $query")

        setLoading(true)
        presenter?.search(query)
    }

    /**
     * Updates the recycler view with images.
     * @param images The images.
     */
    private fun updateImages(images: Array<ImageSearch.Item>) {
        Log.d(TAG, "Update ${images.size}")

        imagesAdapter.update(images)
        setLoading(false)
        rv_images.startLayoutAnimation()
    }

    /**
     * Shows or hides the recycler view and loading spinner accordingly to the loading state.
     * @param loading The loading state. If true, the loading spinner will be shown.
     */
    private fun setLoading(loading: Boolean) {
        if (this.loading != loading) {
            this.loading = loading

            if (loading) {
                rv_images.loadAnimation(R.anim.fade_out, {}) {
                    rv_images.visibility = View.INVISIBLE
                    pb_loading_spinner.visibility = View.VISIBLE
                }
            } else {
                rv_images.visibility = View.VISIBLE
                pb_loading_spinner.visibility = View.INVISIBLE
            }
        }
    }
}
