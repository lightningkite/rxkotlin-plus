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
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.okhttp.HttpClient
import com.lightningkite.rx.okhttp.readJson

import com.lightningkite.rx.mapNotNull
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.ComponentTextBinding
import com.lightningkite.rxexample.databinding.HttpDemoBinding
import com.lightningkite.rx.android.*
import io.reactivex.rxjava3.core.Observable
import kotlinx.serialization.Serializable

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class HttpDemoVG(
    //--- Dependencies (overwritten on flow generation)
    //--- Extends (overwritten on flow generation)
) : ViewGenerator {

    //--- Data
    @Serializable
    data class Post(val userId: Long, val id: Long, val title: String, val body: String)

    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = HttpDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        
        //--- Call
        val call = HttpClient.callWithProgress("https://jsonplaceholder.typicode.com/posts/", parse = { it.readJson<List<Post>>() })

        //--- Set Up xml.progress
        xml.progress.run {
            xml.progress.max =
                10000; call.map { it.approximate }.startWithItem(0f).into(
            xml.progress
        ) { xml.progress.progress = (it * 10000).toInt() }
        }

        //--- Set Up xml.items
        Post(0, 0, "Default", "Failure")
        call
            .mapNotNull { it.response }
            .startWithItem(listOf(Post(0, 0, "Loading...", "-")))
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
