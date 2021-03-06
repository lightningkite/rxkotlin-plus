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
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.removed
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.okhttp.HttpClient
import com.lightningkite.rx.okhttp.WebSocketFrame
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.ComponentTextBinding
import com.lightningkite.rxexample.databinding.WebsocketDemoBinding
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.Subject

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class WebsocketDemoVG : ViewGenerator {

    //--- Data
    val socket = HttpClient.webSocket("wss://ws.ifelse.io").replay(1).refCount()
    val text: ValueSubject<String> = ValueSubject("")

    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = WebsocketDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        //--- Set Up xml.items
        val itemsList = ArrayList<WebSocketFrame>()
        socket.switchMap { it -> it.read }.map { it ->
            println("Adding item")
            itemsList.add(it)
            while (itemsList.size > 20) {
                itemsList.removeAt(0)
            }
            return@map itemsList
        }.startWithItem(itemsList).retry().showIn(xml.items) { observable ->
            //--- Make Subview For xml.items (overwritten on flow generation)
            val cellXml = ComponentTextBinding.inflate(dependency.layoutInflater)
            val cellView = cellXml.root

            //--- Set Up cellXml.label
            observable.map { it.text ?: "<Binary>" }.into(cellXml.label, TextView::setText)
            //--- End Make Subview For xml.items (overwritten on flow generation)
            cellView
        }

        //--- Set Up xml.input
        text.bind<Subject<String>>(xml.input)

        //--- Set Up xml.submit
        xml.submit.setOnClickListener {
            this.socket.firstOrError().into(xml.submit) { it -> it.write.onNext(WebSocketFrame(text = text.value)) }
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
