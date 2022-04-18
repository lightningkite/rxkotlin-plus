# RxKotlin Plus - `view-generator`

## Short Explanation (from main repo readme)

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

## Hello View Generators

Make sure you enable [Android view binding](https://developer.android.com/topic/libraries/view-binding) in your project.

Make an XML for your view called `basic_example.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
>
    <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_margin="8dp"
            android:text="Hello View Generators!"
    />
    <TextView
            android:id="@+id/theNumber"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_margin="8dp"
            android:text="0"
    />
    <Button
            android:id="@+id/incrementTheNumber"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_margin="8dp"
            android:text="Increment Number"
    />
</LinearLayout>
```

Now we make that view generator:

```kotlin
class BasicExampleVG(startNumber: Int): ViewGenerator {
    
    // Store data out here as an Rx something.
    val myNumber = ValueSubject(startNumber)
    
    override fun generate(access: ActivityAccess): View {
        val xml = BasicExampleBinding.inflate(dependency.activity.layoutInflater)
        val view = xml.root

        myNumber
            .map { it.toString() }
            .subscribeAutoDispose(xml.theNumber, TextView::setText)
        
        xml.incrementTheNumber.setOnClickListener {
            myNumber.value++
        }
        
        return view
    }
}
```

Make a main activity like the following:

```kotlin
class MainActivity : ViewGeneratorActivity(R.style.AppTheme) {
    companion object {
        val viewData: BasicExampleVG by lazy { BasicExampleVG(0) }
    }

    override val main: ViewGenerator get() = viewData
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // All the stuff in this function can be done in an Application, alternatively.
        // Set up ViewPump to get custom attributes to work on regular views
        ViewPump.init(
            SpinnerStyleInterceptor,
            FocusOnStartupInterceptor
        )
        
        // Set up activity detection
        ApplicationAccess.applicationIsActiveStartup(application)
        
        // If using all libraries:
        // Set up a static reference to resolve content
        staticApplicationContext = applicationContext
        // Set up HttpClient schedulers
        HttpClient.ioScheduler = Schedulers.io()
        HttpClient.responseScheduler = AndroidSchedulers.mainThread()
        
        super.onCreate(savedInstanceState)
    }

    // More ViewPump setup
    private var appCompatDelegate: AppCompatDelegate? = null
    override fun getDelegate(): AppCompatDelegate {
        if (appCompatDelegate == null) {
            appCompatDelegate = ViewPumpAppCompatDelegate(
                super.getDelegate(),
                this
            )
        }
        return appCompatDelegate!!
    }
}
```

Run the project.  Notice how the number sticks around even if you rotate your screen.

## Stacks

### Make a stack of View Generators

It's literally just a `ValueSubject` of a list of `ViewGenerator`.

```kotlin
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.viewgenerators.ViewGenerator
//...
val myStack = ValueSubject(listOf<ViewGenerator>())
```

### Add a SwapView to your layout

```xml
<com.lightningkite.rx.viewgenerators.SwapView
            android:id="@+id/mainContent"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
```

### Bind the SwapView to the stack

```kotlin
val xml = SomeLayoutBinding.inflate(...)
myStack.showIn(xml.mainContent)
```

### Push a view to the stack

```kotlin
myStack.push(BasicExampleVG(2))
```

### Pop a view from the stack

```kotlin
myStack.pop()
```

### Change the stack in an arbitrary way

```kotlin
myStack.value = myStack.value.toMutableList().apply {
    removeAt(lastIndex)
    add(BasicExampleVG(2))
}
```

### Connect the back button and get intents

Make your root `ViewGenerator` implement `EntryPoint`

```kotlin
import com.lightningkite.rx.viewgenerators.EntryPoint
class RootVG(): ViewGenerator, EntryPoint {
    //...
    override val mainStack: ViewGeneratorStack? get() = myStack
    override fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>){
        //...
    }
    override fun onBackPressed(): Boolean = super.onBackPressed()
}
```

## ActivityAccess 

Some examples of use:

```kotlin
fun test(access: ActivityAccess) {
    access.requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) { granted ->
        if(granted) { /* do something */ }
        else { /* do something else */ }
    }
    access.startIntent(Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))) { resultCode, data ->
        /* do something with result */
    }
    access.openUrl("https://google.com")
    access.openAndroidAppOrStore("com.google.android.apps.messaging")
    access.geocode("Logan, Utah").subscribeBy(onSuccess = {}, onError = {}).forever()
    access.geocode(41.7370, -111.8338).subscribeBy(onSuccess = {}, onError = {}).forever()
    access.requestLocation().subscribeBy(onSuccess = {}, onError = {}).forever()
    access.requestImageGallery().subscribeBy(onSuccess = {}, onError = {}).forever()
}
```

## ApplicationAccess

Here are some conveniences:

- `ApplicationAccess.foreground: Observable<Boolean>` - whether or not the app is in the foreground
- `ApplicationAccess.softInputActive: Observable<Boolean>` - whether or not the keyboard is up
- 