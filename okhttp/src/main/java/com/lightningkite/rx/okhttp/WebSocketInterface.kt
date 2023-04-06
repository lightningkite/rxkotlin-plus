package com.lightningkite.rx.okhttp

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableCallbacks

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
    val write: ObservableCallbacks<WebSocketFrame>
}