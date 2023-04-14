//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.api

import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.delay
import com.badoo.reaktive.single.singleOf
import com.lightningkite.rxexample.models.Post
import java.util.concurrent.TimeUnit

class APIMock() : APIInterface {

    companion object {
        var delayMs: Long = 150
        val lorem =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }

    override fun getExamplePosts(): Single<List<Post>> {
        return singleOf(listOf(Post(title = "First", body = APIMock.lorem))).delay(1000L, mainScheduler)
    }
}
