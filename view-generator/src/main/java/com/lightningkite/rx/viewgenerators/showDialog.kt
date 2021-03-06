//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

import com.lightningkite.rx.android.resources.ViewString
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * The last dialog displayed for testing purposes.
 */
val lastDialog = BehaviorSubject.create<DialogRequest>()

/**
 * A show dialog event.  Available for testing purposes and used by the activity to show dialogs.
 */
val showDialogEvent: PublishSubject<DialogRequest> = PublishSubject.create()

/**
 * A request to show a dialog, optionally with a confirmation action.
 */
class DialogRequest(
    val string: ViewString,
    val confirmation: (()->Unit)? = null
)

/**
 * Shows a very basic dialog as requested.
 */
fun showDialog(request: DialogRequest) {
    lastDialog.onNext(request)
    showDialogEvent.onNext(request)
}

/**
 * Shows a very basic dialog as requested.
 */
fun showDialog(message: ViewString) {
    showDialog(DialogRequest(string = message))
}
