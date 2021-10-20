//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.LayoutInflater
import android.view.View
import com.lightningkite.rxexample.R
import com.lightningkite.rxexample.databinding.LoginDemoBinding
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.bindString
import com.lightningkite.rx.android.onClick
import com.lightningkite.rx.android.resources.ViewStringRaw
import com.lightningkite.rx.android.showLoading
import com.lightningkite.rx.viewgenerators.*
import io.reactivex.rxjava3.subjects.Subject

class LoginDemoVG(
    val stack: ViewGeneratorStack
) : ViewGenerator {
    override val titleString: ViewStringRaw get() = ViewStringRaw("Log In Demo")

    val username = ValueSubject("")
    val password = ValueSubject("")
    val verifyPassword = ValueSubject("")
    val agree = ValueSubject(false)
    val loading = ValueSubject(false)

    override fun generate(dependency: ActivityAccess): View {
        val xml = LoginDemoBinding.inflate(dependency.activity.layoutInflater)
        val view = xml.root

        username.bind<Subject<String>>(xml.username)
        password.bind<Subject<String>>(xml.password)
        verifyPassword.bind<Subject<String>>(xml.verifyPassword)
        agree.bind<Subject<Boolean>>(xml.agree)
        loading.showLoading(xml.submitLoading)
        xml.submit.onClick {
            this.submit()
        }

        return view
    }

    fun submit() {
        if(username.value.isBlank()) {
            showDialog(ViewStringRaw("Username cannot be blank"))
            return
        }
        if(password.value.isBlank()) {
            showDialog(ViewStringRaw("Password cannot be blank"))
            return
        }
        if(verifyPassword.value.isBlank()) {
            showDialog(ViewStringRaw("Verify Password cannot be blank"))
            return
        }
        if(password.value != verifyPassword.value) {
            showDialog(ViewStringRaw("Passwords don't match"))
            return
        }
        if(!agree.value) {
            showDialog(ViewStringRaw("You must agree to the terms"))
            return
        }
        println("Submit!")
        this.loading.value = true
        delay(1000) {
            this.loading.value = false
            this.stack.push(ExampleContentVG())
        }
    }
}
