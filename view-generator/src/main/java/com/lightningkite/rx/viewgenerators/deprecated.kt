package com.lightningkite.rx.viewgenerators


@Deprecated("Use new convention", ReplaceWith("obs.showIn(this, dependency)", "com.lightningkite.rx.viewgenerators.showIn"), DeprecationLevel.ERROR)
fun <T : ViewGenerator> SwapView.bindStack(dependency: ActivityAccess, obs: StackSubject<T>): Unit = throw NotImplementedError()

@Deprecated("Use new convention", ReplaceWith("obs.showIn(this, dependency)", "com.lightningkite.rx.viewgenerators.showIn"), DeprecationLevel.ERROR)
fun <T : ViewGenerator> SwapView.bindStackWithAnimation(
    dependency: ActivityAccess,
    obs: StackSubject<Pair<T, StackTransition>>
): Unit = throw NotImplementedError()