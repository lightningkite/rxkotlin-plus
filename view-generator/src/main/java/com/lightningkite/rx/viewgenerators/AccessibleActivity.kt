package com.lightningkite.rx.viewgenerators

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * An activity that implements [ActivityAccess].
 */
abstract class AccessibleActivity : AppCompatActivity(), ActivityAccess {

    override val activity: Activity
        get() = this
    override val context: Context
        get() = this

    override var savedInstanceState: Bundle? = null

    override val onResume = PublishSubject.create<Unit>()
    override val onPause = PublishSubject.create<Unit>()
    override val onSaveInstanceState = PublishSubject.create<Bundle>()
    override val onLowMemory = PublishSubject.create<Unit>()
    override val onDestroy = PublishSubject.create<Unit>()
    override val onActivityResult = PublishSubject.create<Triple<Int, Int, Intent?>>()
    override val onNewIntent = PublishSubject.create<Intent>()
//    override val isNetworkAvailable: ValueSubject<Boolean> get() = ValueSubject(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState

    }
    override fun onResume() {
        super.onResume()
        onResume.onNext(Unit)
    }
    override fun onPause() {
        onPause.onNext(Unit)
        super.onPause()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceState.onNext(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        onLowMemory.onNext(Unit)
    }

    override fun onDestroy() {
        onDestroy.onNext(Unit)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        onNewIntent.onNext(intent)
        super.onNewIntent(intent)
    }

    val requestReturns: HashMap<Int, (Map<String, Int>) -> Unit> = HashMap()

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    override fun prepareOnResult(presetCode: Int, onResult: (Int, Intent?) -> Unit): Int {
        returns[presetCode] = onResult
        return presetCode
    }

    @Suppress("OverrideDeprecatedMigration")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResult.onNext(Triple(requestCode, resultCode, data))
        returns[requestCode]?.invoke(resultCode, data)
        returns.remove(requestCode)
    }

    override fun performBackPress() {
        this.onBackPressed()
    }

    /**
     * Requests a bunch of permissions and returns a map of permissions that were previously ungranted and their new status.
     */
    override fun requestPermissions(permission: Array<String>): Single<Set<String>> = Single.create { em ->
        val ungranted = permission.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            val generated: Int = (Math.random() * 0xFFFF).toInt()

            requestReturns[generated] = { em.onSuccess(permission.filter {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }.toSet()) }

            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), generated)

        } else {
            em.onSuccess(permission.filter {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }.toSet())
        }
    }

    /**
     * Requests a single permissions and returns whether it was granted or not.
     */
    override fun requestPermission(permission: String): Single<Boolean> = Single.create<Boolean> { em ->
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            val generated: Int = (Math.random() * 0xFFFF).toInt()
            requestReturns[generated] = {
                em.onSuccess(it[permission] == PackageManager.PERMISSION_GRANTED)
            }
            ActivityCompat.requestPermissions(this, arrayOf(permission), generated)
        } else {
            em.onSuccess(true)
        }
    }.cache()

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT >= 23) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val map = HashMap<String, Int>()
            for (i in permissions.indices) {
                map[permissions[i]] = grantResults[i]
            }
            requestReturns[requestCode]?.invoke(map)

            requestReturns.remove(requestCode)
        }
    }
}
