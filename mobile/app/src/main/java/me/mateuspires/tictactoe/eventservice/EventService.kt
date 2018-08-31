package me.mateuspires.tictactoe.eventservice

import io.reactivex.Observable
import me.mateuspires.tictactoe.data.model.*

interface EventService {

    fun connect()

    fun disconnect()

    fun sendIntro(intro: IntroMessage)

    fun sendMovement(movement: MovementMessage)

    fun getWaitingObservable(): Observable<Boolean>

    fun getStartObservable(): Observable<StartMessage>

    fun getStateObservable(): Observable<StateMessage>

    fun getWinnerObservable(): Observable<WinnerMessage>

    fun getCloseObservable(): Observable<CloseMessage>

    fun getConnectionObservable(): Observable<Boolean>
}
