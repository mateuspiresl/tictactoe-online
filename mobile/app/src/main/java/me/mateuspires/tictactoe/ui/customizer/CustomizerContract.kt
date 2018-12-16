package me.mateuspires.tictactoe.ui.customizer

import io.reactivex.Observable
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.game.Player

interface CustomizerContract {

    interface Presenter {

        fun getXImageItem(): ImageSearch.Item?

        fun getYImageItem(): ImageSearch.Item?

        fun getSearchResultsObservable(): Observable<ImageSearch.Result>

        fun search(query: String)

        fun selectImage(image: ImageSearch.Item)

        fun unselect()

        fun done()
    }

    interface View {

        fun onImageAttach(player: Player, image: ImageSearch.Item)

        fun onImageDetach(player: Player)
    }

    interface ImageSearchAdapter {

        fun update(images: Array<ImageSearch.Item>)

        fun unselect()
    }
}
