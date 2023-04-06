package com.lightningkite.rx.okhttp

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks
import com.badoo.reaktive.subject.publish.PublishSubject
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString

/**
 * A live web socket connection.
 */
class ConnectedWebSocket(val url: String) : WebSocketListener(), WebSocketInterface {
    init {
        println("Web socket to $url attempted")
    }
    internal var underlyingSocket: WebSocket? = null
    private val _read =
        PublishSubject<WebSocketFrame>()
    private val _ownConnection = PublishSubject<WebSocketInterface>()

    override val ownConnection: Observable<WebSocketInterface> get() = _ownConnection.let { HttpClient.threadCorrectly(it) }

    override val read: Observable<WebSocketFrame> get() = _read.let { HttpClient.threadCorrectly(it) }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("Web socket to $url opened")
        _ownConnection.onNext(this)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        try {
            println("Web socket to $url failed $t")
            t.printStackTrace()
            _ownConnection.onError(t)
            _read.onError(t)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Web socket to $url closing code $code")
        _ownConnection.onComplete()
        _read.onComplete()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Web socket to $url got message $text")
        _read.onNext(WebSocketFrame(text = text))
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Web socket to $url got message $bytes")
        _read.onNext(WebSocketFrame(binary = bytes.toByteArray()))
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("Web socket to $url closed code $code")
    }

    override val write: ObservableCallbacks<WebSocketFrame> = object : ObservableCallbacks<WebSocketFrame> {
        override fun onComplete() {
            underlyingSocket?.close(1000, null)
        }

        override fun onNext(frame: WebSocketFrame) {
            frame.text?.let {
                underlyingSocket?.send(it)
            }
            frame.binary?.let { binary ->
                underlyingSocket?.send(binary.toByteString())
            }
        }

        override fun onError(error: Throwable) {
            underlyingSocket?.close(1011, error.message)
        }
    }
}