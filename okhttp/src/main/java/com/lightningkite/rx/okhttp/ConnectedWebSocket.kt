package com.lightningkite.rx.okhttp

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString

/**
 * A live web socket connection.
 */
class ConnectedWebSocket(val url: String) : WebSocketListener(), Observer<WebSocketFrame> {
    internal var underlyingSocket: WebSocket? = null
    private val _read =
        PublishSubject.create<WebSocketFrame>()
    private val _ownConnection = PublishSubject.create<ConnectedWebSocket>()

    /**
     * An observable representing the socket's connection.  Will emit once it is fully connected.
     */
    val ownConnection: Observable<ConnectedWebSocket> get() = _ownConnection.let { HttpClient.threadCorrectly(it) }

    /**
     * Messages that come through the websocket.
     */
    val read: Observable<WebSocketFrame> get() = _read.let { HttpClient.threadCorrectly(it) }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        _ownConnection.onNext(this)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        try {
            _ownConnection.onError(t)
            _read.onError(t)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        _ownConnection.onComplete()
        _read.onComplete()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        _read.onNext(WebSocketFrame(text = text))
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        _read.onNext(WebSocketFrame(binary = bytes.toByteArray()))
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
    }

    override fun onComplete() {
        underlyingSocket?.close(1000, null)
    }

    override fun onSubscribe(d: Disposable) {
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