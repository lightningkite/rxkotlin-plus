package com.lightningkite.rx

import com.badoo.reaktive.subject.behavior.BehaviorSubject

@Deprecated("Use BehaviorSubject", replaceWith = ReplaceWith("BehaviorSubject", "com.badoo.reaktive.subject.behavior.BehaviorSubject"), DeprecationLevel.ERROR)
typealias HasValueSubject<T> = BehaviorSubject<T>

@Deprecated("Use BehaviorSubject", replaceWith = ReplaceWith("BehaviorSubject", "com.badoo.reaktive.subject.behavior.BehaviorSubject"), DeprecationLevel.ERROR)
typealias ValueSubject<T> = BehaviorSubject<T>