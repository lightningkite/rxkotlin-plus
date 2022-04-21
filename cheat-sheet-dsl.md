# Android View DSL Cheat Sheet

It's built for total minimalism, so here's basically the whole thing:

## Ground Rules

- All sizes (besides text sizes) are measure in `dp`.
- Default spacing is applied unless you override it.

## Using the DSL

```kotlin
context.dsl {
    text { setText("Hello world!") }
}
```

## Styles

There is no style system for this View DSL, so instead it imports and exposes [Paris](https://github.com/airbnb/paris) by AirBnB for such purposes.

Example:

```kotlin
text {
    style(R.style.myTextStyle)    
}
```

## Views

### Columns - `fun ViewDSL.column{verticalGravity}{horizontalGravity}(...views)`

Creates a vertical `LinearLayout` with the views in the given order, with the given default gravity.

If the vertical gravity is unspecified, the function's gravity is `top`.

`Fill` can be given in place of the horizontal gravity to make all views have a width of `MATCH_PARENT` by default.  Otherwise, default size for both axes is `MATCH_PARENT`. 

Example:

```kotlin
columnTopStart(
    text { setText("First") },
    text { setText("Second") },
    text { setText("Third") },
    setup = {
        // Set up your LinearLayout here.
    }
)
```


### Rows - `fun ViewDSL.row{verticalGravity}{horizontalGravity}(...views): LinearLayout`

Creates a horizontal `LinearLayout` with the views in the given order, with the given default gravity.

If the horizontal gravity is unspecified, the function's gravity is `start`.

`Fill` can be given in place of the vertical gravity to make all views have a width of `MATCH_PARENT` by default.  Otherwise, default size for both axes is `MATCH_PARENT`. 

Example:

```kotlin
rowTopStart(
    text { setText("First") },
    text { setText("Second") },
    text { setText("Third") },
    setup = {
        // Set up your LinearLayout here.
    }
)
```


### Frames - `fun ViewDSL.frame(...views): FrameLayout`

Creates a frame layout containing the given views.

Default size for children is `MATCH_PARENT` for both axes.

Example:

```kotlin
frame(
    text { 
        gravity = Grav.topCenter
        wrapSize()
        setText("Top") 
    },
    text { 
        gravity = Grav.bottomLeft
        wrapSize()
        setText("Bottom Left") 
    },
    text { 
        setText("Cover Everything")
        // MATCH_PARENT by default
    },
    setup = {
        // Set up your FrameLayout here.
    }
)
```


### Flipper - `fun ViewDSL.flipper(...views): ViewFlipper`

Creates a view flipper containing the given views.

Default size for children is `MATCH_PARENT` for both axes.

Example:

```kotlin
flipper(
    text { setText("Main Content") },
    setup = {
        // Set up your ViewFlipper here.
    }
)
```


### Scroll Views

Creates a scroll view in the given direction.

Example:

```kotlin
scroll(columnTopFill(
    text { setText("First") },
    text { setText("Second") },
    text { setText("Third") },
))
horizontalScroll(rowStartFill(
    text { setText("First") },
    text { setText("Second") },
    text { setText("Third") },
))
```


### Other Views - `fun ViewDSL.{ViewTypeName}(setup: ViewType.()->Unit): ViewType`

Creates a view of the given type and allows you to set it up.

View types with a `View` prefix/suffix have the word dropped for brevity's sake.

Example:

```kotlin
text {
    setText("Some Text")
    setTextColor(0xFF008800.toInt())
}
```


## Layout Parameters

By default, all views have a default layout margin of 8dp.  The exceptions are layout views, specifically:

- `row`
- `column`
- `frame`
- `scroll`
- `horizontalScroll`
- `flipper`
- `recycler`
- `pager`
- `swap`

You can override the default layout parameters with the following functions:

- `var View.weight: Float` - Sets the `weight` for `LinearLayout`s such as `row` and `column`.
- `var View.gravity: Int` - Sets the alignment of the item inside the parent `LinearLayout`.
- `fun View.setSize(value: Int)` - - Sets the width and height to the given measurement in density pixels.
- `fun View.matchSize()` - Sets the width and height to match the width of its parent.
- `fun View.wrapSize()` - Sets the width and height to wrap its content.
- `fun View.setWidth(value: Int)` - - Sets the width to the given measurement in density pixels.
- `fun View.matchWidth()` - Sets the width to match the width of its parent.
- `fun View.wrapWidth()` - Sets the width to wrap its content.
- `fun View.setHeight(value: Int)` - Sets the height to the given measurement in density pixels.
- `fun View.matchHeight()` - Sets the height to match the width of its parent.
- `fun View.wrapHeight()` - Sets the height to wrap its content.
- `fun View.margin(value: Int)` - Sets the margin of all sides individually in density pixels.
- `fun View.updateMargins(left: Int = noChange, top: Int = noChange, right: Int = noChange, bottom: Int = noChange)` - Sets the margin of each side individually in density pixels.
- `fun View.pad(value: Int)` - Sets the margin of all sides individually in density pixels.
- `fun View.updatePad(left: Int = noChange, top: Int = noChange, right: Int = noChange, bottom: Int = noChange)` - Sets the margin of each side individually in density pixels.

Example:

```kotlin
columnTopStart(
    text {
        setText("Hello world!")
        gravity = Gravity.START
    },
    text {
        setText("I'm at the end.")
        gravity = Gravity.END
    },
)
```


## Shortcuts

- `Grav` gives you shortcuts to gravity constants
- `InputTypes` gives you shortcuts to complete input types