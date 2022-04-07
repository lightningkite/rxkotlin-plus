//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.ViewFlipper
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.resources.ViewStringRaw
import com.lightningkite.rx.android.showLoading
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.LoginDemoBinding

class LoginDemoVG(
    val stack: ViewGeneratorStack
) : ViewGenerator {

    val username = ValueSubject("")
    val password = ValueSubject("")
    val verifyPassword = ValueSubject("")
    val agree = ValueSubject(false)
    val loading = ValueSubject(false)

    override fun generate(dependency: ActivityAccess): View {
        val xml = LoginDemoBinding.inflate(dependency.activity.layoutInflater)
        val view = xml.root

        username.bind(xml.username)
        password.bind(xml.password)
        verifyPassword.bind(xml.verifyPassword)
        agree.bind(xml.agree)
        loading.into(xml.submitLoading, ViewFlipper::showLoading)
        xml.submit.setOnClickListener {
            this.submit()
        }

        return view
    }

    fun submit() {
        if (username.value.isBlank()) {
            showDialog(ViewStringRaw("Username cannot be blank"))
            return
        }
        if (password.value.isBlank()) {
            showDialog(ViewStringRaw("Password cannot be blank"))
            return
        }
        if (verifyPassword.value.isBlank()) {
            showDialog(ViewStringRaw("Verify Password cannot be blank"))
            return
        }
        if (password.value != verifyPassword.value) {
            showDialog(ViewStringRaw("Passwords don't match"))
            return
        }
        if (!agree.value) {
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
