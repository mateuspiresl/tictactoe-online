package me.mateuspires.tictactoe.eventservice

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.mateuspires.tictactoe.data.model.*
import org.json.JSONObject

class SocketIOEventService(
        private val socket: Socket = IO.socket("http://192.168.0.14") // http://tictactoe.mateuspires.me
) : EventService {

    override fun connect() {
        socket.connect()
    }

    override fun disconnect() {
        socket.disconnect()
    }

    override fun sendIntro(intro: IntroMessage) {
        socket.emit("intro", JSONObject(Gson().toJson(intro)))
    }

    override fun sendMovement(movement: MovementMessage) {
        socket.emit("movement", JSONObject(Gson().toJson(movement)))
    }

    override fun getWaitingObservable(): Observable<Boolean> {
        return Observable.create { emitter ->
            socket.on("waiting") { emitter.onNext(true) }
        }
    }

    override fun getStartObservable(): Observable<StartMessage> {
        return createObservable("start", StartMessage::class.java)
    }

    override fun getStateObservable(): Observable<StateMessage> {
        return createObservable("state", StateMessage::class.java)
    }

    override fun getWinnerObservable(): Observable<WinnerMessage> {
        return createObservable("winner", WinnerMessage::class.java)
    }

    override fun getCloseObservable(): Observable<CloseMessage> {
        return createObservable("close", CloseMessage::class.java)
    }

    override fun getConnectionObservable(): Observable<Boolean> {
        return Observable.create { emitter ->
            socket.on(Socket.EVENT_CONNECT) { emitter.onNext(true) }
            socket.on(Socket.EVENT_DISCONNECT) { emitter.onNext(false) }
        }
    }

    private fun <T> createObservable(event: String, type: Class<T>): Observable<T> {
        val observable: Observable<T> = Observable.create { emitter ->
            socket.on(event) { it ->
                emitter.onNext(Gson().fromJson(it[0] as String, type))
            }
        }

        return observable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
