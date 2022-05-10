package com.oxygen.ktx_ui.livedata

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.oxygen.ktx.safeLet

fun <T> mutableLiveDataOf(value: T? = null) =
    MutableLiveData<T>().apply { value?.apply { postValue(this) } }

fun <T> Fragment.observe(liveData: LiveData<T>?, observer: Observer<T>) =
    liveData?.observe(this.viewLifecycleOwner, observer)

fun <T> AppCompatActivity.observe(liveData: LiveData<T>?, observer: Observer<T>) =
    liveData?.observe(this, observer)

inline fun <reified X, reified Y, R> LiveData<X>.withLatest(
    other: LiveData<Y>,
    crossinline mapFunction: (X, Y) -> R
): LiveData<R> {
    val latestValuesBySource = mutableMapOf<LiveData<*>, Any?>()

    val emitLatestChangeIfPossible: (MediatorLiveData<R>) -> Unit = { mediator ->
        if (latestValuesBySource.size == 2) {
            safeLet(
                latestValuesBySource[this@withLatest] as X,
                latestValuesBySource[other] as Y
            ) { first, second ->
                mediator.value = mapFunction(first, second)
            }
        }
    }

    return MediatorLiveData<R>().apply {
        addSource(this@withLatest) {
            latestValuesBySource[this@withLatest] = it as? Any
            emitLatestChangeIfPossible(this)
        }
        addSource(other) {
            latestValuesBySource[other] = it as? Any
            emitLatestChangeIfPossible(this)
        }
    }
}

fun <T, R> List<LiveData<T>>.combineLatest(skipFirstEvents: Boolean = false, combiner: (List<T?>) -> R): LiveData<R> {
    val latestValues = mutableMapOf<LiveData<T>, T>()
    val combineIfNeeded: (MediatorLiveData<R>) -> Unit = { mediatorLiveData ->
        if (!skipFirstEvents || (skipFirstEvents && latestValues.size == this.size)) {
            mediatorLiveData.value = combiner.invoke(latestValues.values.toList())
        }
    }
    val mediator = MediatorLiveData<R>()

    forEach { item ->
        mediator.addSource(item) {
            latestValues[item] = it
            combineIfNeeded(mediator)
        }
    }

    return  mediator
}

fun <X, Y, R> LiveData<X>.merge(source: LiveData<Y>, mapFunction: (X?, Y?) -> R): LiveData<R> {
    return MediatorLiveData<R>().apply {
        addSource(this@merge) { valueOfSource1 ->
            val valueOfSource2 = source.value
            value = mapFunction(valueOfSource1, valueOfSource2)
        }
        addSource(source) { valueOfSource2 ->
            val valueOfSource1 = this@merge.value
            value = mapFunction(valueOfSource1, valueOfSource2)
        }
    }
}

fun <T> combine(vararg sources: LiveData<T>): LiveData<T> {
    val mediatorLiveData = MediatorLiveData<T>()
    sources.forEach {
        mediatorLiveData.apply {
            addSource(it) { value = it }
        }
    }
    return mediatorLiveData
}

fun <S, T> MediatorLiveData<S>.addSources(vararg args: LiveData<T>, observer: (T) -> Unit) {
    args.forEach {
        addSource(it) { source ->
            observer.invoke(source)
        }
    }
}

fun isDataPresent(vararg sources: LiveData<out Any?>): LiveData<Boolean> {
    val valueBySource = mutableMapOf<LiveData<out Any?>, Any?>()
    val mediatorLiveData = MediatorLiveData<Boolean>().apply {
        value = false
    }

    sources.forEach { source ->
        valueBySource[source] = null
        mediatorLiveData.apply {
            addSource(source) {
                valueBySource[source] = it
                value = valueBySource.values.filterNotNull().size == sources.size
            }
        }
    }

    return mediatorLiveData
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    var isInitialized = false
    return MediatorLiveData<T>().apply {
        addSource(this@distinctUntilChanged) {
            if (!isInitialized || it != value) {
                value = it
            }
            if (!isInitialized) {
                isInitialized = true
            }
        }
    }
}

