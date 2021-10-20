//! This file will translate using Khrysalis.
package com.lightningkite.rx.viewgenerators

/**
 * Implement this interface on your [ViewGenerator] to add a custom action to occur when the back button is pressed.
 */
interface HasBackAction {
    /**
     * @return if the back press was handled.
     */
    fun onBackPressed(): Boolean = false
}