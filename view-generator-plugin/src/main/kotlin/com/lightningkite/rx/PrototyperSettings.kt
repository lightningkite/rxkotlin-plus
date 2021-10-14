package com.lightningkite.rx

object PrototyperSettings {
    var verbose = false
}

fun log(text: String){
    if(PrototyperSettings.verbose){
        println(text)
    }
}
