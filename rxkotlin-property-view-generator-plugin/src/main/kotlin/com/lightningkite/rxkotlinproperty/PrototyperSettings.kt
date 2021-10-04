package com.lightningkite.rxkotlinproperty

object PrototyperSettings {
    var verbose = false
}

fun log(text: String){
    if(PrototyperSettings.verbose){
        println(text)
    }
}
