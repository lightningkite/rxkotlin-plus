package com.lightningkite.rxkotlinproperty.android

import com.google.android.material.tabs.TabLayout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the tabs as well as the selected tab to the data provided.
 * tabs is the title of each tab and it will create a tab for each of them.
 * selected is the tab number that will be selected. User selecting a tab
 * will update selected, as well modifying selected will manifest in the tabs.
 *
 */

fun <T : Any> TabLayout.bind(
    tabs: List<T>,
    selected: Subject<T>,
    allowReselect: Boolean = false,
    toString: (T) -> String = { it.toString() }
) {
    this.removeAllTabs()
    for (tab in tabs) {
        addTab(newTab().setText(tab.let(toString)))
    }
    selected.subscribeBy { value ->
        val index = tabs.indexOf(value)
        if (index != -1) {
            this.getTabAt(index)?.select()
        }
    }.addTo(this.removed)
    this.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

        var suppress = false

        override fun onTabReselected(p0: TabLayout.Tab) {
            if (!suppress && allowReselect) {
                suppress = true
                selected.onNext(tabs[p0.position])
                suppress = false
            }
        }

        override fun onTabUnselected(p0: TabLayout.Tab) {}

        override fun onTabSelected(p0: TabLayout.Tab) {
            if (!suppress) {
                suppress = true
                selected.onNext(tabs[p0.position])
                suppress = false
            }
        }
    })
}
