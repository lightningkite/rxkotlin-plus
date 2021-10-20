//! This file will translate using Khrysalis.
//
// ExternalTestVG.swift
// Created by Butterfly Prototype Generator
// Sections of this file can be replaces if the marker, '(overwritten on flow generation)', is left in place.
//
package com.lightningkite.rxexample.vg

//--- Imports

import android.view.View
import com.lightningkite.rx.viewgenerators.ActivityAccess
import java.time.*
import java.time.format.*
import java.time.*
import java.time.format.*
import java.time.*
import java.time.format.*
import java.time.*
import java.time.format.*
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.ExternalTestBinding
import java.util.*
import java.time.*

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class ExternalTestVG(
    //--- Dependencies (overwritten on flow generation)
    //--- Extends (overwritten on flow generation)
) : ViewGenerator {
    
    
    //--- Title (overwritten on flow generation)
    override val titleString: ViewString get() = ViewStringRaw("External Test")
    
    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = ExternalTestBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        
        //--- Set Up xml.scrollView (overwritten on flow generation)
        
        //--- Set Up xml.openMap
        xml.openMap.onClick {
            dependency.openMap(41.7269, -111.8432, "Lightning Kite", 14f)
        }

        //--- Set Up xml.openWeb
        xml.openWeb.onClick {
            dependency.openUrl("https://lightningkite.com")
        }

        //--- Set Up xml.openEvent
        xml.openEvent.onClick {
            dependency.openEvent(
                title = "A Virtual Lunch with LK",
                description = "Come eat virtual food with us!",
                location = "Lightning Kite in Logan Utah",
                start = ZonedDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0),
                end = ZonedDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0)
            )
        }

        //--- Generate End (overwritten on flow generation)
        
        return view
    }
    
    //--- Init

    init {
        //--- Init End
    }

    //--- Actions

    //--- Action openMapClick
    //--- Action openWebClick
    //--- Action openEventClick

    //--- Body End
}
