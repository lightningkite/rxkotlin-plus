//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.viewgenerators

import com.lightningkite.rxkotlinproperty.android.resources.ViewString
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

val lastDialog = BehaviorSubject.create<DialogRequest>()
val showDialogEvent: PublishSubject<DialogRequest> = PublishSubject.create()

class DialogRequest(
    val string: ViewString,
    val confirmation: (()->Unit)? = null
)

fun showDialog(request: DialogRequest) {
    lastDialog.onNext(request)
    showDialogEvent.onNext(request)
}

fun showDialog(message: ViewString) {
    showDialog(DialogRequest(string = message))
}
