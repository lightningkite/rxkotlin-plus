package com.lightningkite.rx.android

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.publish.PublishSubject
import com.lightningkite.rx.bind
import com.lightningkite.rx.withWrite


fun TextView.textChanges(): Observable<CharSequence> {
    return TextViewTextChangesObservable(this)
}

private class TextViewTextChangesObservable(
    private val view: TextView
) : Observable<CharSequence> {

    private class Listener(
        private val view: TextView,
        private val observer: ObservableObserver<CharSequence>
    ) : TextWatcher, Disposable {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!isDisposed) {
                observer.onNext(s)
            }
        }

        override fun afterTextChanged(s: Editable) {
        }

        private var disposed: Boolean = false
        override val isDisposed: Boolean
            get() = disposed

        override fun dispose() {
            if (!disposed) {
                disposed = true
                view.removeTextChangedListener(this)
            }
        }
    }

    override fun subscribe(observer: ObservableObserver<CharSequence>) {
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.addTextChangedListener(listener)
    }

}


/**
 * Binds the value of this to the internal text of the EditText.
 * Changes will propagate both directions.
 *
 * Example:
 * val name = ValueSubject("Jason")
 * name.bind(editTextView)
 */
fun <SOURCE : Subject<String>> SOURCE.bind(editText: EditText): SOURCE {

    var suppress = false
    CompositeDisposable().apply {
        add(this@bind.subscribe { value ->
            if (!suppress) {
                suppress = true
                editText.setText(value)
                suppress = false
            }
        })
        add(editText
            .textChanges()
            .map { it.toString() }
            .subscribe { value ->
                if (!suppress) {
                    suppress = true
                    onNext(value)
                    suppress = false
                }
            })
    }
        .addTo(editText.removed)
    return this
}
