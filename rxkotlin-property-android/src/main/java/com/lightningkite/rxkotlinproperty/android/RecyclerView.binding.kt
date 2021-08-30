package com.lightningkite.rxkotlinproperty.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lightningkite.rxkotlinproperty.*
import kotlin.reflect.KClass

/**
 *
 *  Provides the RecyclerView a lambda to call when the lambda reaches the end of the list.
 *
 */

fun RecyclerView.whenScrolledToEnd(action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            (layoutManager as? LinearLayoutManager)?.let {
                if (it.findLastVisibleItemPosition() == adapter?.itemCount?.minus(1)) {
                    action()
                }
            }
        }
    })
}

/**
 *
 *  When set to true will reverse the direction of the recycler view.
 *  Rather than top to bottom, it will scroll bottom to top.
 *
 */
var RecyclerView.reverseDirection: Boolean
    get() = (this.layoutManager as? LinearLayoutManager)?.reverseLayout ?: false
    set(value){
        (this.layoutManager as? LinearLayoutManager)?.reverseLayout = value
    }

private fun RecyclerView.defaultLayoutManager(){
    if(layoutManager == null) {
        layoutManager = LinearLayoutManager(context)
    }
}

/**
 *
 * Binds the data in the RecyclerView to the data provided by the Observable.
 * makeView is the lambda that creates the view tied to each item in the list of data.
 *
 * Example
 * val data = StandardProperty(listOf(1,2,3,4,5))
 * recycler.bind(
 *  data = data,
 *  defaultValue = 0,
 *  makeView = { observable ->
 *       val xml = ViewXml()
 *       val view = xml.setup(dependency)
 *       view.text.bindString(obs.map{it -> it.toString()})
 *       return view
 *       }
 * )
 */

fun <T> RecyclerView.bind(
    data: Property<List<T>>,
    defaultValue: T,
    makeView: (Property<T>) -> View
) {
    defaultLayoutManager()
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }.until(this@bind.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = StandardProperty<T>(defaultValue)
            val subview = makeView(event)
            subview.setRemovedCondition(this@bind.removed)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = data.value.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? StandardProperty<T>)?.let {
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}

class RVTypeHandler(val context: Context) {
    class Handler(
        val type: KClass<*>,
        val defaultValue: Any,
        val handler: (Property<Any>)->View
    )
    internal var typeCount: Int = 0
        private set
    private val handlers: ArrayList<Handler> = ArrayList<Handler>()
    private val defaultHandler: Handler = Handler(
        type = Any::class,
        defaultValue = Unit,
        handler = { obs ->
            View(context)
        }
    )

    fun handle(type: KClass<*>, defaultValue: Any, action: (Property<Any>)->View ) {
        handlers += Handler(
            type = type,
            defaultValue = defaultValue,
            handler = action
        )
        typeCount++
    }
    inline fun <reified T: Any> handle(defaultValue: T, noinline action: (Property<T>)->View ) {
        handle(T::class, defaultValue) { obs ->
            action(obs.map { it as T })
        }
    }

    internal fun type(item: Any): Int {
        handlers.forEachIndexed { index, handler ->
            if(handler.type.isInstance(item)){
                return index
            }
        }
        return typeCount
    }
    internal fun make(type: Int): View {
        val handler = if(type < typeCount) handlers[type] else defaultHandler
        val event = StandardProperty<Any>(handler.defaultValue)
        val subview = handler.handler(event)
        subview.tag = event
        subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        return subview
    }
}

fun RecyclerView.bindMulti(
    data: Property<List<Any>>,
    typeHandlerSetup: (RVTypeHandler)->Unit
) {
    val typeHandler = RVTypeHandler(this.context).apply(typeHandlerSetup)
    defaultLayoutManager()
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }.until(this@bindMulti.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val subview = typeHandler.make(viewType)
            subview.setRemovedCondition(this@bindMulti.removed)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemViewType(position: Int): Int {
            return typeHandler.type(data.value.getOrNull(position) ?: return typeHandler.typeCount)
        }
        override fun getItemCount(): Int = data.value.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? MutableProperty<Any>)?.let {
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}


/**
 *
 * Binds the data in the RecyclerView to the data provided by the Observable.
 * This is designed for multiple types of views in the recycler view.
 * determineType is a lambda with an item input. The returned value is an Int determined by what view that item needs.
 * makeView is the lambda that creates the view for the type determined
 *
 * Example
 * val data = StandardProperty(listOf(item1,item2,item3,item4,item5))
 * recycler.bind(
 *  data = data,
 *  defaultValue = Item(),
 *  determineType: { item ->
 *      when(item){
 *          ... return 1
 *          ... return 2
 *      }
 *  },
 *  makeView = { type, item ->
 *      when(type){
 *       1 -> {
 *          val xml = ViewXml()
 *          val view = xml.setup(dependency)
 *          view.text.bindString(item.map{it -> it.toString()})
 *          return view
 *            }
 *       2 -> {
 *          .... return view
 *            }
 *          }
 *      }
 * )
 */

fun <T> RecyclerView.bindMulti(
    data: Property<List<T>>,
    defaultValue: T,
    determineType: (T)->Int,
    makeView: (Int, Property<T>) -> View
) {
    defaultLayoutManager()
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }.until(this@bindMulti.removed)
        }

        override fun getItemViewType(position: Int): Int {
            return determineType(data.value[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = StandardProperty<T>(defaultValue)
            val subview = makeView(viewType, event)
            subview.setRemovedCondition(this@bindMulti.removed)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = data.value.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? StandardProperty<T>)?.let {
                it.value = data.value[position]
            } ?: run {
                println("Failed to find property to update")
            }
        }
    }
}


/**
 *
 *
 *
 */

fun RecyclerView.bindRefresh(
    loading: Property<Boolean>,
    refresh: () -> Unit
) {
    (this.parent as? SwipeRefreshLayout)?.run {
        loading.subscribeBy { value ->
            this.post {
                this.isRefreshing = value
            }
        }.until(this@bindRefresh.removed)
        setOnRefreshListener {
            refresh()
        }
    }
}

