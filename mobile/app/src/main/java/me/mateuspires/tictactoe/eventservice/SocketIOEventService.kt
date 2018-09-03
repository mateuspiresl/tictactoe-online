package me.mateuspires.tictactoe.eventservice

import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.mateuspires.tictactoe.data.model.*
import org.json.JSONObject

class SocketIOEventService : EventService {

    companion object {
        private const val TAG = "TTT.SocketIO"
        private const val HOST = "http://tictactoe.mateuspires.me"
    }

    private val socket: Socket = IO.socket(HOST)
    private var connected = false

    override fun connect() {
        Log.d(TAG, "Connecting")
        connected = true
        socket.connect()
    }

    override fun disconnect() {
        if (connected) {
            Log.d(TAG, "Disconnecting")
            connected = false
            socket.disconnect()
        }
    }

    override fun sendIntro(intro: IntroMessage) {
        emit("intro", intro)
    }

    override fun sendMovement(movement: MovementMessage) {
        emit("movement", movement)
    }

    override fun getWaitingObservable(): Observable<Boolean> {
        return createObservable("waiting", Boolean::class.java, true)
    }

    override fun getStartObservable(): Observable<StartMessage> {
        return createObservable("start", StartMessage::class.java)
    }

    override fun getStateObservable(): Observable<StateMessage> {
        return createObservable("state", StateMessage::class.java)
    }

    override fun getEndObservable(): Observable<EndMessage> {
        return createObservable("end", EndMessage::class.java)
    }

    override fun getCloseObservable(): Observable<CloseMessage> {
        return createObservable("close", CloseMessage::class.java)
    }

    override fun getConnectionObservable(): Observable<Boolean> {
        return createObservable { emitter ->
            socket.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Event connect")
                connected = true
                emitter.onNext(true)
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "Event disconnected")
                connected = false
                emitter.onNext(false)
            }

            socket.on(Socket.EVENT_ERROR) {
                Log.d(TAG, "Event error")
                connected = false
                emitter.onNext(false)
            }
        }
    }

    private fun emit(event: String, data: Any) {
        val json = JSONObject(Gson().toJson(data))
        Log.d(TAG, "Emitting to $event, $json")
        socket.emit(event, json)
    }

    private fun <T> createObservable(action: (ObservableEmitter<T>) -> Unit): Observable<T> {
        val observable: Observable<T> = Observable.create { emitter ->
            action(emitter)
        }

        return observable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun <T> createObservable(event: String, type: Class<T>, constValue: T? = null)
            : Observable<T> {
        return createObservable { emitter ->
            socket.on(event) { it ->
                if (constValue == null) {
                    Log.d(TAG, "Event $event, ${it[0] as JSONObject}")
                    emitter.onNext(Gson().fromJson((it[0] as JSONObject).toString(), type))
                } else {
                    Log.d(TAG, "Event $event")
                    emitter.onNext(constValue)
                }
            }
        }
    }
}
