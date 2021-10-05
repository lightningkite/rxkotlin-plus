package com.lightningkite.rxkotlinproperty.android

import android.widget.RatingBar
import com.lightningkite.rxkotlinproperty.MutableProperty
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

/**
 *
 * Binds the rating bar to the property provided, as well allows you to provide how
 * many stars the rating is out of. Any changes to the rating bar will update the property value,
 * as well any change to the property directly will update the rating bar.
 *
 * Example
 * val rating:MutableProperty<Int> = StandardProperty(5)
 * ratingBar.bind(5, rating)
 *
 */

fun RatingBar.bind(
    stars: Int,
    property: MutableProperty<Int>
) {
    this.max = stars
    this.numStars = stars
    this.incrementProgressBy(1)

    var suppress = false
    property.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.progress = value
            suppress = false
        }
    }.until(this.removed)
    this.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
        if (!suppress) {
            suppress = true
            property.value = p0.progress
            suppress = false
        }
    }

}

/**
 *
 * Binds the rating bar to the property provided, as well allows you to provide how
 * many stars the rating is out of. The value cannot be changed from the rating bar itself,
 * though any change to the property directly will manifest in the rating bar.
 *
 * Example
 * val rating: Property<Int> = StandardProperty(5)
 * ratingBar.bind(5, rating)
 *
 */

fun RatingBar.bind(
    stars: Int,
    property: Property<Int>
) {
    this.max = stars
    this.numStars = stars
    this.setIsIndicator(true)

    property.subscribeBy { value ->
        this.progress = value
    }.until(this.removed)
}



/**
 *
 * Binds the rating bar to the property provided, as well allows you to provide how
 * many stars the rating is out of. Any changes to the rating bar will update the property value,
 * as well any change to the property directly will update the rating bar..
 *
 * Example
 * val rating: MutableProperty<Float> = StandardProperty(5.0f)
 * ratingBar.bind(5.0f, rating)
 *
 */

fun RatingBar.bindFloat(
    stars: Int,
    property: MutableProperty<Float>
) {
    this.numStars = stars
    this.stepSize = 0.01f

    var suppress = false
    property.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.rating = value
            suppress = false
        }
    }.until(this.removed)
    this.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
        if (!suppress) {
            suppress = true
            property.value = p1
            suppress = false
        }
    }

}


/**
 *
 * Binds the rating bar to the property provided, as well allows you to provide how
 * many stars the rating is out of. The value cannot be changed from the rating bar itself,
 * though any change to the property directly will manifest in the rating bar.
 *
 * Example
 * val rating: Property<Float> = StandardProperty(5.0f)
 * ratingBar.bind(5.0f, rating)
 *
 */

fun RatingBar.bindFloat(
    stars: Int,
    property: Property<Float>
) {
    this.numStars = stars
    this.setIsIndicator(true)

    property.subscribeBy { value ->
        this.rating = value
    }.until(this.removed)
}
