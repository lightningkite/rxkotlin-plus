package com.lightningkite.rx.viewgenerators

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.FocusFinder
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy

/**
 * An [AccessibleActivity] that is structured to contain a single [ViewGenerator].
 * In addition,
 *  - Watches the keyboard going in and out
 *  - Passes deep links to the [ViewGenerator]
 */
abstract class ViewGeneratorActivity(val changeToTheme: Int? = null) : AccessibleActivity() {

    /**
     * The main ViewGenerator to render.  You probably want to store it statically rather than directly in here.
     */
    abstract val main: ViewGenerator

    lateinit var view: View
    private var showDialogEventCloser: Disposable? = null
    private var animator: ValueAnimator? = null

    open fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>) {
        (main as? EntryPoint)?.handleDeepLink(schema, host, path, params)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            intent?.let { handleNewIntent(it) }
        }
        view = main.generate(this)
        showDialogEventCloser = showDialogEvent.subscribeBy { request ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage(request.string.get(this))
            request.confirmation?.let { conf ->
                builder.setPositiveButton(android.R.string.ok) { dialog, _ -> conf.invoke(); dialog.dismiss() }
                builder.setNeutralButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            } ?: run {
                builder.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            }
            builder
                .create()
                .show()
        }
        changeToTheme?.let { setTheme(it) }

        setContentView(view)
    }

    override fun onDestroy() {
        showDialogEventCloser?.dispose()
        super.onDestroy()
    }

    private var suppressKeyboardChange = false
    private var keyboardSubscriber: Disposable? = null
    private val keyboardTreeObs: ViewTreeObserver.OnGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val keyboardHeight = resources.displayMetrics.heightPixels - rect.bottom
        if (keyboardHeight.toFloat() > resources.displayMetrics.heightPixels * 0.15f) {
            suppressKeyboardChange = true
            ApplicationAccess.softInputActive.onNext(true)
            suppressKeyboardChange = false
        } else {
            delay(30L) {
                suppressKeyboardChange = true
                ApplicationAccess.softInputActive.onNext(false)
                suppressKeyboardChange = false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        animator = ValueAnimator().apply {
            setIntValues(0, 100)
            duration = 10000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            var last = System.currentTimeMillis()
            addUpdateListener {
                val now = System.currentTimeMillis()
                animationFrame.onNext((now - last) / 1000f)
                last = now
            }
            start()
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(keyboardTreeObs)
        keyboardSubscriber = ApplicationAccess.softInputActive.subscribe {
            if (!suppressKeyboardChange) {
                view.post {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (it) {
                        if (currentFocus == null) {
                            FocusFinder.getInstance().findNextFocus(view as ViewGroup, view, View.FOCUS_DOWN)
                        }
                        currentFocus?.let {
                            imm.showSoftInput(it, 0)
                        }
                    } else {
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
    }

    override fun onPause() {
        view.viewTreeObserver.removeOnGlobalLayoutListener(keyboardTreeObs)
        keyboardSubscriber?.dispose()
        keyboardSubscriber = null
        animator?.pause()
        animator = null
        super.onPause()
    }

    protected open fun handleNewIntent(intent: Intent) {
        intent.data?.let { uri ->
            handleDeepLink(uri)
        }
        intent.extras?.getString("deepLink")?.let { Uri.parse(it) }?.let {
            handleDeepLink(it)
        }
    }

    protected fun handleDeepLink(uri: Uri) {
        handleDeepLink(
            uri.scheme ?: "",
            uri.host ?: "",
            uri.path ?: "",
            uri.queryParameterNames.mapNotNull {
                uri.getQueryParameter(it)?.let { p -> it to p }
            }.associate { it }
        )
    }

    override fun onNewIntent(intent: Intent) {
        handleNewIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
//        val toClose = view.findCloseOrBack()
//        if (toClose != null) {
//            toClose.performClick()
//        } else {
            val main = main
            if (main !is HasBackAction || !main.onBackPressed()) {
                super.onBackPressed()
            }
//        }
    }

//    private val backIds = setOf(R.id.close, R.id.closeButton, R.id.back, R.id.backButton, R.id.backArrow)
//    private fun View.findCloseOrBack(): View? {
//        if (this.id in backIds) return this
//        else if (this is ViewGroup) {
//            for (i in this.childCount - 1 downTo 0) {
//                val result = getChildAt(i).findCloseOrBack()
//                if (result != null) {
//                    return result
//                }
//            }
//        }
//        return null
//    }
}
