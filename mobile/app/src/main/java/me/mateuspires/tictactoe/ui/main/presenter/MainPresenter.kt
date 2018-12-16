package me.mateuspires.tictactoe.ui.main.presenter

import me.mateuspires.tictactoe.data.models.PlayersImages
import me.mateuspires.tictactoe.data.persistence.PlayersImagesRepository
import me.mateuspires.tictactoe.game.Game
import me.mateuspires.tictactoe.ui.main.MainContract

class MainPresenter(
        private val view: MainContract.View,
        private val repository: PlayersImagesRepository
) : MainContract.Presenter {

    private val game: Game = Game()
    private var playing: Boolean = false

    /**
     * Returns the players images.
     * @return The players images.
     */
    override fun getPlayersImages(): PlayersImages {
        return repository.load()
    }

    /**
     * Starts a new game.
     */
    override fun startNewGame() {
        playing = true
        game.clear()
        view.onUpdate(game.isXTurn(), game.getBoard())
    }

    /**
     * Makes a movement.
     * @param position The position to mark.
     */
    override fun move(position: Int): Boolean {
        return if (playing) {
            when {
                game.move(position) -> {
                    playing = false
                    view.onWin(game.isXTurn(), game.getBoard())
                }

                game.isBoardFull() -> {
                    playing = false
                    view.onTie(game.getBoard())
                }

                else -> {
                    view.onUpdate(game.isXTurn(), game.getBoard())
                }
            }

            true
        } else {
            false
        }
    }

    override fun isPlaying(): Boolean {
        return playing
    }
}