# The RxKotlin Plus Cheat Sheet

## Quick Rx Definitions

`Observable<T>`
: An event emitter, whose events are of type `T`.

`Subject<T>`
: An event emitter that you can either listen to OR send new events on.

`ValueSubject<T>`
: A property that notifies listeners when the value changes.  Essentially a `BehaviorSubject` with a guaranteed starting value.

`Observable.just(something)`
: An observable that just gives you `something` then stops.


## Attaching Views to Rx primitives

### A view's property should match the value from an `Observable`

```kotlin
myObservable.subscribeAutoDispose(myView, ViewType::property OR ViewType::setter)

// some common examples

someTextObservable.subscribeAutoDispose(textView, TextView::setText)
someRatioObservable.subscribeAutoDispose(progressBar, ProgressBar::progressRatio)
someBooleanObservable.subscribeAutoDispose(view, View::exists)  // visibility = VISIBLE or GONE
someBooleanObservable.subscribeAutoDispose(view, View::visible)  // visibility = VISIBLE or INVISIBLE
someBooleanObservable.subscribeAutoDispose(viewFlipper, ViewFlipper::showLoading)

// You can use the 'into' alias as well.

someTextObservable.into(textView, TextView::setText)
someRatioObservable.into(progressBar, ProgressBar::progressRatio)
someBooleanObservable.into(view, View::exists)  // visibility = VISIBLE or GONE
someBooleanObservable.into(view, View::visible)  // visibility = VISIBLE or INVISIBLE
someBooleanObservable.into(viewFlipper, ViewFlipper::showLoading)
```

### Do an action when an `Observable` updates

```kotlin
someObservable.subscribeAutoDispose(view) { value ->
    // Do something with `value`
}
someObservable.into(view) { value ->
    // Do something with `value`
}
```

### A view is used as an input for a `Subject`

Typically used with a `ValueSubject`.

There are multiple overloads for `bind` depending on the type.

```kotlin
mySubject.bind(myView)

myStringSubject.bind(editText)
myIntSubject.bind(seekBar, 1..10)
myFloatSubject.bind(seekBar)  // Binds between 0f and 1f smoothly
myBooleanSubject.bind(toggleableView)  // CheckBox, RadioButton, ToggleButton
myLocalDate.bind(button)  // Opens date selector
myLocalTime.bind(button)  // Opens time selector
myLocalDateTime.bind(button)  // Opens both
```

### A view displays a list of data from an `Observable`

```kotlin
myListSubject.showIn(recyclerView) { singleValueObservable: Observable<T> ->
    val xml = RowBinding.inflate(dependency.layoutInflater)
    singleValueObservable.map { it.toString() }.subscribeAutoDispose(xml.title, TextView::setText)
    return@showIn xml.root
}
myListSubject.showIn(linearLayout) { obs -> /*...*/ }
myListSubject.showIn(viewPager) { obs -> /*...*/ }
myListSubject.showIn(spinner, selectedValueSubject) { item -> item.toString() }
myListSubject.showIn(autoCompleteTextView, { itemSelected -> }) { item -> item.toString() }
```

### Use the current value from an observable when clicking

```kotlin
myView.onClick(observable) { it: ValueFromObservable -> /* do something */ }
```

## Commonly used transformations

### A view's property should match a property from the value inside an `Observable`

```kotlin
data class Post(val title: String, val content: String)
val postObs: Observable<Post> = getPost()
postObs.map { it.title }.subscribeAutoDispose(titleView, TextView::setText)
postObs.map { it.content }.subscribeAutoDispose(contentView, TextView::setText)
```

### Bind a number subject to an `EditText`
```kotlin
myIntSubject.toSubjectString().bind(editText)
myDoubleSubject.toSubjectString().bind(editText)
```

## `ViewGenerator`

### Sample

```kotlin
class MyViewGenerator(): ViewGenerator {
    // Place the properties used in your view here, usually in the form of ValueSubjects
    val email = ValueSubject("")
    
    // Write the function that shows a view for this data
    override fun generate(dependency: ActivityAccess): View {
        // Inflate your XML here or generate your UI
        // whatever you want, I'm not a cop
        val xml = MyViewBinding.inflate(dependency.layoutInflater)
        
        // Bind your views to data
        this.email.bind(xml.emailField)
        
        // Finally, return the root view
        return xml.root
    }
}
```

### A stack

Use a [`SwapView`](https://github.com/lightningkite/rxkotlin-plus/blob/master/view-generator/src/main/java/com/lightningkite/rx/viewgenerators/SwapView.kt) and [`ViewGenerator`](https://github.com/lightningkite/rxkotlin-plus/blob/master/view-generator/src/main/java/com/lightningkite/rx/viewgenerators/ViewGenerator.kt).

```kotlin
val viewStack = ValueSubject<List<ViewGenerator>>(listOf())
viewStack.push(MyViewGenerator())
viewStack.pop()
viewStack.value = listOf(MyViewGenerator(), OtherViewGenerator())
viewStack.showIn(swapView, dependency)
```

### Interacting with the Android Activity

Access to an Android `Activity` comes through the `ActivityAccess` a `ViewGenerator` is given.

```kotlin
 override fun generate(dependency: ActivityAccess): View {
    //...
    button.setOnClickListener { 
        dependency.activity  // the activity itself
        dependency.onResume.subscribeAutoDispose(view) { 
            println("Resuming the activity")
        }
        dependency.onPause.subscribeAutoDispose(view) { 
            println("Pausing the activity")
        }
        dependency.requestPermission(aPermission).subscribeAutoDispose(view) { granted -> 
            println("Permission was granted? $granted")
        }
        dependency.startIntentRx(intent, options).subscribeAutoDispose(view) { result -> 
            println("Got code ${result.code} with intent ${result.intent}")
        }
        dependency.geocode("Logan Utah").subscribeAutoDispose(view) {
            println("Geocoded to latitude ${it.firstOrNull()?.latitude}")
        }
        dependency.geocode(37.0, 96.0).subscribeAutoDispose(view) {
            println("Geocoded to country ${it.firstOrNull()?.countryName}")
        }
        dependency.share("My Stuff", url = "https://lightningkite.com")
        dependency.openUrl("https://lightningkite.com")
        dependency.openMap(37.0, 96.0, label = "United States")
        dependency.openEvent(
            title = "Lightning Kite Meet 'n Greet",
            description = "Come meet us!",
            location = "Logan, UT",
            start = ZonedDateTime.of(2022, 8, 25, 12, 0, 0, 0, ZoneId.of("MST")),
            end = ZonedDateTime.of(2022, 8, 25, 14, 0, 0, 0, ZoneId.of("MST")),
        )
        dependency.requestLocation().subscribeAutoDispose(view) {
            println("You are at ${it.latitude}, ${it.longitude}")
        }
        dependency.requestImageGallery().subscribeAutoDispose(view) { it: Uri ->
            println("Got image $it")
        }
    }
    //...
}
```