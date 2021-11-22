package com.lightningkite.rx.okhttp

import kotlinx.serialization.Serializable
import org.junit.Test

@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

class CodableTest {
    @Test
    fun postTest() {
        HttpClient.call("https://jsonplaceholder.typicode.com/posts", method = HttpClient.POST, body = Post(1, 1, "Test", "Content").toJsonRequestBody())
            .readJson<Post>()
            .blockingGet()
            .let { println(it) }
    }
}