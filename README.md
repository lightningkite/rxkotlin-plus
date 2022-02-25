# RxKotlin Plus

A set of libraries to use RxKotlin in your Android projects effectively, mostly extenstion functions.

Uses Jake Wharton's RxBinding and ViewPump.

All are available in Maven Central under `com.lightningkite.rx`. 

MIT License

# Packages

## rxplus

The core package, which contains some convenient extension functions for manipulating `Observable` and `Subject`.

Most important features:

- `ValueSubject<T>` - a `BehaviorSubject` that guarantees an initial value.  Also has the property `value` for getting/setting.
- `Subject.map` - allows bidirectional mapping of a subject.
- `Observable.withWrite` - transforms an `Observable` into a `Subject` by providing some action to perform when `onNext` is called.

## android-bindings

Contains a bunch of functions for bidirectional binding of Android widgets to `Subject`s, as well as a `CompositeDisposable` attached to a view's lifecycle.

Some sample features:

- `View.removed` - a `CompositeDisposable` that is disposed when the view is removed from the hierarchy
- `Observable.subscribeAutoDispose(TextView, TextView::setText)` - show the value in the given view and keep it updated, automatically disposed when the view is removed from the hierarchy
- `Subject<String>.bind(EditText)` - bidirectional bind to the subject, automatically disposed when the view is removed from the hierarchy
- `Observable<List<T>>.showIn(RecyclerView) { obs: Observable<T> -> View }` - Shows updating content in a `RecyclerView`

## android-resources

Adds an abstraction around strings, images, and videos for displaying conveniently.  These are particularly useful when you are showing values from observables, as you frequently need to show a resource while the data loads and then replace it with an image once finished.

Also helpful for unit testing, as `ViewString` is not directly dependent on Android so your tests can run in the JVM.

Sample:

```kotlin
fun test(imageView: ImageView) {
    imageView.setImage(ImageRemoteUrl("https://picsum.photos/200"))
}
```

## okhttp

Provides a more convenient interface to OkHttp using Rx.  Also handles JSON encoding.

Sample:

```kotlin
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

Adds a way to upload `Image` from the `android-resources` package.

```kotlin
HttpClient.call(
    url = "https://test.com/upload-image",
    method = HttpClient.POST,
    body = myImage.toRequestBody()
)...
```

## view-generator

An alternate way of handling view navigation in Android.  Essentially a replacement for `Fragment`.

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

You may have noticed that this means we can use constructors in our views.  This is one of the biggest advantages of using view generators.

We have been using this pattern for Android apps for a long time at Lightning Kite now and it has proven to be both stable and easier to maintain.

[Read more here](view-generator/README.md)


## view-generator-plugin

A Gradle plugin that will read your Android layout XML files and automatically generate `ViewGenerator` classes for you, creating a full click-through design of your app that can be edited into a full app.

[Read more here](view-generator-plugin/PrototypeGenerator.md)

# Philosophy

The entire goal of this repository is to make Android programming more convenient while keeping the code understandable to an outsider.  As such, we:

- Avoid making our own, unfamiliar interfaces where possible
- Use independent extension functions where possible
- "Glass Pool" philosophy - you should be able to see through the top layer that you use down to the bottom with ease

# Status

This is a very actively-maintained project as we use this for many production projects at Lightning Kite.  It is rapidly stabilizing as we finish moving our existing projects over to it, but it is all based on past libraries we've had.  This is simply the consolodation and clean up of such libraries. 

## History

While this repository is new, the parts of this library have been used at Lightning Kite in production applications since around 2017.  The names and signatures have changed, as well as the intended publicity level, but this is the final repository and naming convention for the public-facing tools we use at Lightning Kite for Android apps.

Previous versions contained more tools and more interfaces than was desired.  In this version, we removed as many interfaces as possible and used more extension functions.  In the end, we had managed to remove over half of the code and drastically simplify many interfaces.

## Versioning

This repository follows semantic versioning after version `0.1.0`.  Deprecations will be introduced where possible with automatic replacements.

## Connection to Khrysalis

Khrysalis is a tool that transpiles Android Kotlin code into iOS Swift and web Typescript code.  This library is the Kotlin side of similar libraries in iOS and Web.  This set of libraries, however, is fully intended to be used both with and without Khrysalis.

## Roadmap

While the library has all the intended functionality at the moment, there is always more room for documentation, growth, and testing.

- [ ] Additional documentation on existing classes
- [ ] Instrumented tests for bindable components
- [ ] Additional Tutorials
  - [ ] Convenient unit testing using ViewGenerators