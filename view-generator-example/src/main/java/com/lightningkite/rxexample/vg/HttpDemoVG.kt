//! This file will translate using Khrysalis.
//
// HttpDemoVG.swift
// Created by Butterfly Prototype Generator
// Sections of this file can be replaces if the marker, '(overwritten on flow generation)', is left in place.
//
package com.lightningkite.rxexample.vg

//--- Imports

import android.view.View
import android.widget.TextView
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull
import com.badoo.reaktive.observable.startWithValue
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.okhttp.HttpClient
import com.lightningkite.rx.okhttp.readJson
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.ComponentTextBinding
import com.lightningkite.rxexample.databinding.HttpDemoBinding
import kotlinx.serialization.Serializable

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class HttpDemoVG : ViewGenerator {

    //--- Data
    @Serializable
    data class Post(val userId: Long, val id: Long, val title: String, val body: String)

    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = HttpDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        //--- Call
        val call = HttpClient.callWithProgress(
            "https://jsonplaceholder.typicode.com/posts/",
            parse = { it.readJson<List<Post>>() })

        //--- Set Up xml.progress
        xml.progress.run {
            xml.progress.max =
                10000; call.map { it.approximate }.startWithValue(0f).into(
            xml.progress
        ) { xml.progress.progress = (it * 10000).toInt() }
        }

        //--- Set Up xml.items
        Post(0, 0, "Default", "Failure")
        call
            .mapNotNull { it.response }
            .startWithValue(listOf(Post(0, 0, "Loading...", "-")))
            .showIn(xml.items) { observable ->
                //--- Make Subview For xml.items
                val cellXml = ComponentTextBinding.inflate(dependency.layoutInflater)
                val cellView = cellXml.root

                //--- Set Up cellXml.label
                observable.map { it -> it.title }
                    .into(cellXml.label, TextView::setText)
                //--- End Make Subview For xml.items
                cellView
            }

        //--- Generate End (overwritten on flow generation)

        return view
    }

    //--- Init

    init {
        //--- Init End
    }

    //--- Actions


    //--- Body End
}
