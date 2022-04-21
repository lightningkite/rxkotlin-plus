# RxKotlin Plus

[![release status](https://github.com/lightningkite/rxkotlin-plus/actions/workflows/release.yml/badge.svg)](https://s01.oss.sonatype.org/content/repositories/releases/com/lightningkite/rx/rxplus/)
[![snapshot status](https://github.com/lightningkite/rxkotlin-plus/actions/workflows/snapshot.yml/badge.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/lightningkite/rx/rxplus/)

Current version: `1.0.0`

A set of libraries to use RxKotlin in your Android projects effectively, mostly extenstion functions.

Uses Jake Wharton's RxBinding and ViewPump.

All are available in Maven Central under `com.lightningkite.rx`.

MIT License

```kotlin
Observable.interval(1000L, TimeUnit.MILLISECONDS)
    .map { "$it seconds have passed" }
    .into(secondsPassedTextView, TextView::setText)
```

# Examples

There's a full sample project
in [view-generator-example](https://github.com/lightningkite/rxkotlin-plus/tree/master/view-generator-example/src/main/java/com/lightningkite/rxexample)
.

One view I'd point out in particular is the [login demonstration](), which shows how to make a typical login page.

In addition, check out our [cheat sheet](cheat-sheet.md) page for a bunch of individual feature examples!

# Packages

## rxplus

```kotlin
implementation("com.lightningkite.rx:rxplus:$VERSION")
```

The core package, which contains some convenient extension functions for manipulating `Observable` and `Subject`.

Most important features:

- `ValueSubject<T>` - a `BehaviorSubject` that guarantees an initial value. Also has the property `value` for
  getting/setting.
- `Subject.map` - allows bidirectional mapping of a subject.
- `Observable.withWrite` - transforms an `Observable` into a `Subject` by providing some action to perform when `onNext`
  is called.

## android-bindings

```kotlin
implementation("com.lightningkite.rx:android-bindings:$VERSION")
```

Contains a bunch of functions for bidirectional binding of Android widgets to `Subject`s, as well as
a `CompositeDisposable` attached to a view's lifecycle.

Some sample features:

- `View.removed` - a `CompositeDisposable` that is disposed when the view is removed from the hierarchy
- `Observable.subscribeAutoDispose(TextView, TextView::setText)` - show the value in the given view and keep it updated,
  automatically disposed when the view is removed from the hierarchy
- `Observable.into(TextView, TextView::setText)` - shorthand for the former
- `Subject<String>.bind(EditText)` - bidirectional bind to the subject, automatically disposed when the view is removed
  from the hierarchy
- `Observable<List<T>>.showIn(RecyclerView) { obs: Observable<T> -> View }` - Shows updating content in a `RecyclerView`

## android-resources

```kotlin
implementation("com.lightningkite.rx:android-resources:$VERSION")
```

Adds an abstraction around strings, images, and videos for displaying conveniently. These are particularly useful when
you are showing values from observables, as you frequently need to show a resource while the data loads and then replace
it with an image once finished.

Also helpful for unit testing, as `ViewString` is not directly dependent on Android so your tests can run in the JVM.

Sample:

```kotlin
fun test(imageView: ImageView) {
    imageView.setImage(ImageRemoteUrl("https://picsum.photos/200"))
}
```

## okhttp

```kotlin
implementation("com.lightningkite.rx:okhttp:$VERSION")
```

Provides a more convenient interface to OkHttp using Rx. Also handles JSON encoding.

Sample:

```kotlin
@Serializable
data class Todo(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)

HttpClient.call("https://jsonplaceholder.typicode.com/todos/1")
    .readJson<Todo>()
    .subscribeBy(
        onError = { e -> e.printStackTrace() },
        onSuccess = { it -> println("Got $it") }
    )
    .addTo(compositeDisposable)

HttpClient.call(
    url = "https://jsonplaceholder.typicode.com/todos",
    method = HttpClient.POST,
    body = Todo(1, 2, "Test", false).toJsonRequestBody(),
    headers = mapOf("Authorization", "jwt asdfasdf")
)
    .readJson<Todo>()
    .subscribeBy(
        onError = { e -> e.printStackTrace() },
        onSuccess = { it -> println("Got $it") }
    )
    .addTo(compositeDisposable)
```

## okhttp-resources

```kotlin
implementation("com.lightningkite.rx:okhttp-resources:$VERSION")
```

Adds a way to upload `Image` from the `android-resources` package.

```kotlin
HttpClient.call(
    url = "https://test.com/upload-image",
    method = HttpClient.POST,
    body = myImage.toRequestBody()
)...
```

## view-generator

```kotlin
implementation("com.lightningkite.rx:view-generator:$VERSION")
```

An alternate way of handling view navigation in Android. Essentially a replacement for `Fragment`.

Built to be bare-bones, a `ViewGenerator` has the following interface:

```kotlin
interface ViewGenerator {
    fun generate(dependency: ActivityAccess): android.view.View
}
```

where `ActivityAccess` is an interface for accessing an `Activity` and its callbacks.

You can then use a `SwapView` to display a stack of view generators:

```kotlin
val myStack = ValueSubject<List<ViewGenerator>>(listOf())
myStack.showIn(swapView, activityAccess)
```

Now, pushing and popping views onto the stack is really easy:

```kotlin
myStack.push(SomeViewGenerator("Test Data"))  // push is a shortcut function
myStack.pop()  // pop is a shortcut function
myStack.value = listOf(MyViewGenerator(myStack))  // You can reset the whole stack easily
```

You may have noticed that this means we can use constructors in our views. This is one of the biggest advantages of
using view generators.

We have been using this pattern for Android apps for a long time at Lightning Kite now and it has proven to be both
stable and easier to maintain.

[Read more here](view-generator/README.md)

## view-generator-plugin

A Gradle plugin that will read your Android layout XML files and automatically generate `ViewGenerator` classes for you,
creating a full click-through design of your app that can be edited into a full app.

[Read more here](view-generator-plugin/PrototypeGenerator.md)

## dsl

```kotlin
implementation("com.lightningkite.rx:dsl:$VERSION")
```

A view DSL comparable to Anko, but built on the principle of both API and implementation minimalism. As such, it
currently sits around 400 lines of code with no expected additions.

It is built as an alternative to using XML layouts in an effort to improve localization of concerns, but both methods
are completely supported.

**The DSL is still marked experimental as of yet, and has not been used in production code. However, it has worked
extremely well for several prototypes. As stated above, no changes are expected, but that doesn't mean they won't happen
and I'm not willing to guarantee backwards compatibility yet.**

It is planned for use in a production project soon, so it should be finalized in late 2022 along with equivalent DSLs
for iOS and Web.

See it's [cheat sheet here](cheat-sheet-dsl.md).

Sample:

```kotlin
// A login page
context.dsl {
    scroll(
        columnCenter(
            image {
                size(256)
                setImageResource(R.drawable.ic_launcher_foreground)
            },
            text {
                style(R.style.Header)
                setText(R.string.app_name)
            },
            editText {
                width(256)
                inputType = InputTypes.email
                setHint(R.string.email)
            },
            button {
                width(256)
                setText(R.string.log_in_or_sign_up)
                setOnClickListener {
                    TODO()
                }
            }
        )
    )
}
```

# Philosophy

The entire goal of this repository is to make Android programming more convenient while keeping the code understandable
to an outsider. As such, we:

- Avoid making our own, unfamiliar interfaces where possible
- Use independent extension functions where possible
- "Glass Pool" philosophy - you should be able to see through the top layer that you use down to the bottom with ease
- Follow the naming conventions of those that came before

# Status

This is a very actively-maintained project as we use this for many production projects at Lightning Kite. It is rapidly
stabilizing as we finish moving our existing projects over to it, but it is all based on past libraries we've had. This
is simply the consolidation and clean up of such libraries.

## History

While this repository is new, the parts of this library have been used at Lightning Kite in production applications
since around 2017. The names and signatures have changed, as well as the intended publicity level, but this is the final
repository and naming convention for the public-facing tools we use at Lightning Kite for Android apps.

Previous versions contained more tools and more interfaces than was desired. In this version, we removed as many
interfaces as possible and used more extension functions. In the end, we had managed to remove over half of the code and
drastically simplify many interfaces.

## Versioning

This repository follows semantic versioning after version `0.1.0`. Deprecations will be introduced where possible with
automatic replacements.

## Connection to Khrysalis

[Khrysalis](https://github.com/lightningkite/khrysalis) is a tool that transpiles Android Kotlin code into iOS Swift and
web Typescript code. This library is the Kotlin side of similar libraries in iOS and Web. This set of libraries,
however, is fully intended to be used both with and without it.

## Roadmap

While the library has all the intended functionality at the moment, there is always more room for documentation, growth,
and testing.

- Finalize the view DSL
- Additional documentation on existing classes
- Instrumented tests for bindable components
- Additional Tutorials
    - Convenient unit testing using ViewGenerators

# Questions

- Do these libraries exist for other platforms and languages?
    - [iOS/Swift](https://github.com/lightningkite/RxSwiftPlus)
    - [Web/Typescript](https://github.com/lightningkite/rxjs-plus)
- Why are you not using Compose? It's the FUTURE.
    - React-inspired UI frameworks all have the same problem - they have a great 10-line example for things, but get
      very complex when used at scale. The primary source of this difficulty is similar to something
      called [function coloring](http://journal.stuffwithstuff.com/2015/02/01/what-color-is-your-function/).
      Essentially, some functions and ideas can be used inside of `@Composable` functions safely, but some cannot, and
      there is very little help to your average user as to which is which. I spend an absurd amount of time day-to-day
      helping my coworkers solve "When does this run?" issues in both React and Compose. RxJava might have a steeper
      learning curve to start, but in the end, I've found that it is easier and prevents more bugs.
- Why are you using RxJava? It's SO OUTDATED.
    - It's also got tons of backing and support, and has years of help information on it. It's also been standardized at
      this point for a long time, making it easy for programmers to jump between Rx implementations and discuss with
      outsiders.
- Why are you not using Coroutines/Flow?
    - Kotlin Coroutines are still relatively new, and their support has not been fully nailed out yet. For
      example, `flatMap` is still marked as experimental.
    - Kotlin Coroutine Flows have a nearly identical API to RxJava, which makes the switch a net loss as RxJava has much
      more common support.
    - Rx is a common language between other platforms, making it easy to swap platforms with knowledge of Rx in one to
      another. This greatly aids transpilation and more. There are copies of this library for
      both [iOS/Swift](https://github.com/lightningkite/RxSwiftPlus)
      and [Web/Typescript](https://github.com/lightningkite/rxjs-plus).
- Why isn't this done in Kotlin Shared Code?
    - It's built to work with Android's UI, so that would be pretty redundant and require massive amounts of tweaking,
      especially considering [Reaktive](https://github.com/badoo/Reaktive) has less support than RxJava as of yet.
    - Additionally, [Khrysalis](https://github.com/lightningkite/khrysalis) already solves for translating your code
      using these tools to other platforms and does so in a more native way.