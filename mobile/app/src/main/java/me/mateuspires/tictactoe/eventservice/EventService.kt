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

    fun getEndObservable(): Observable<EndMessage>

    fun getCloseObservable(): Observable<CloseMessage>

    fun getConnectionObservable(): Observable<Boolean>
}
