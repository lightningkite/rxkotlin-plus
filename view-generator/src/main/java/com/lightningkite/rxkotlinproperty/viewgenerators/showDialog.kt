//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.viewgenerators

import com.lightningkite.rxkotlinproperty.StandardProperty
import com.lightningkite.rxkotlinproperty.android.resources.ViewString
import io.reactivex.subjects.PublishSubject

val lastDialog = StandardProperty<DialogRequest?>(null)
val showDialogEvent: PublishSubject<DialogRequest> = PublishSubject.create()

class DialogRequest(
    val string: ViewString,
    val confirmation: (()->Unit)? = null
)

fun showDialog(request: DialogRequest) {
    lastDialog.value = request
    showDialogEvent.onNext(request)
}

fun showDialog(message: ViewString) {
    showDialog(DialogRequest(string = message))
}
