//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.api

import com.lightningkite.rxexample.models.Post
import io.reactivex.rxjava3.core.Single

interface APIInterface {
    //https://jsonplaceholder.typicode.com/posts
    fun getExamplePosts(): Single<List<Post>>
}
