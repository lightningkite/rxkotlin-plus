//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.api

import com.badoo.reaktive.single.Single
import com.lightningkite.rxexample.models.Post

interface APIInterface {
    //https://jsonplaceholder.typicode.com/posts
    fun getExamplePosts(): Single<List<Post>>
}
