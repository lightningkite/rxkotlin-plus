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
interface WebSocketInterface {
    /**
     * An observable representing the socket's connection.  Will emit once it is fully connected.
     */
    val ownConnection: Observable<WebSocketInterface>

    /**
     * Messages that come through the websocket.
     */
    val read: Observable<WebSocketFrame>

    /**
     * Messages to send through the socket
     */
    val write: Observer<WebSocketFrame>
}