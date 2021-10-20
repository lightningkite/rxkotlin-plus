//! This file will translate using Khrysalis.
//
// WebsocketDemoVG.swift
// Created by Butterfly Prototype Generator
// Sections of this file can be replaces if the marker, '(overwritten on flow generation)', is left in place.
//
package com.lightningkite.rxexample.vg

//--- Imports

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.okhttp.ConnectedWebSocket
import com.lightningkite.rx.okhttp.HttpClient
import com.lightningkite.rx.okhttp.WebSocketFrame
import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.ComponentTextBinding
import com.lightningkite.rxexample.databinding.WebsocketDemoBinding
import com.lightningkite.rx.android.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class WebsocketDemoVG(
    //--- Dependencies (overwritten on flow generation)
    //--- Extends (overwritten on flow generation)
) : ViewGenerator {


    //--- Title (overwritten on flow generation)
    override val titleString: ViewString get() = ViewStringRaw("Websocket Demo")

    //--- Data
    val socket: Observable<ConnectedWebSocket> = HttpClient.webSocket("wss://echo.websocket.org").replay(1).refCount()
    val text: ValueSubject<String> = ValueSubject("")

    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = WebsocketDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        //--- Set Up xml.items
        val itemsList = ArrayList<WebSocketFrame>()
        WebSocketFrame()
        socket.switchMap { it -> it.read }.map { it ->
            println("Adding item")
            itemsList.add(it)
            while (itemsList.size > 20) {
                itemsList.removeAt(0)
            }
            return@map itemsList as List<WebSocketFrame>
        }.startWithItem(itemsList).retry().showIn(xml.items, makeView = label@{ observable ->
            //--- Make Subview For xml.items (overwritten on flow generation)
            val cellXml = ComponentTextBinding.inflate(dependency.layoutInflater)
            val cellView = cellXml.root

            //--- Set Up cellXml.label (overwritten on flow generation)
            Observable.just("Some Text")
                .subscribeAutoDispose<Observable<String>, TextView, String>(cellXml.label, TextView::setText)
            //--- End Make Subview For xml.items (overwritten on flow generation)
            return@label cellView
        }
        )

        //--- Set Up xml.input
        text.bind<Subject<String>>(xml.input)

        //--- Set Up xml.submit
        xml.submit.onClick {
            this.socket.take(1).subscribe { it -> it.onNext(WebSocketFrame(text = this.text.value)) }
                .addTo(xml.submit.removed)
        }

        //--- Generate End (overwritten on flow generation)

        return view
    }

    //--- Init

    init {
        //--- Init End
    }

    //--- Actions


    //--- Action submitClick (overwritten on flow generation)
    fun submitClick() {
    }

    //--- Body End
}
