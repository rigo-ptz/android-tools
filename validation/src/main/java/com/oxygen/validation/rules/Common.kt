package com.oxygen.validation.rules

import android.util.Patterns
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun TextView.isNotEmpty(
    onSuccess: ((String, View) -> Unit)? = null,
    onError: ((String, View) -> Unit)? = null
): LiveData<Boolean> {
    val subject = MutableLiveData<Boolean>().apply { value = false }
    this.doAfterTextChanged {
        it?.toString()?.let { input ->
            subject.value = input.isNotEmpty()
            if (input.isNotEmpty()) onSuccess?.invoke(input, this) else onError?.invoke(input, this)
        }
    }
    return subject
}

fun TextView.isValidEmail(
    onSuccess: ((String, View) -> Unit)? = null,
    onError: ((String, View) -> Unit)? = null
): LiveData<Boolean> {
    val subject = MutableLiveData<Boolean>().apply { value = false }
    this.doAfterTextChanged {
        it?.toString()?.let { input ->
            val isValidEmail = input.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches()
            subject.value = isValidEmail
            if (isValidEmail) onSuccess?.invoke(input, this) else onError?.invoke(input, this)
        }
    }
    return subject
}

fun TextView.textDiffersFromDefault(
    defaultString: String,
    onSuccess: ((String, View) -> Unit)? = null,
    onError: ((String, View) -> Unit)? = null
): LiveData<Boolean> {
    val subject = MutableLiveData<Boolean>().apply { value = false }
    this.doAfterTextChanged {
        it?.toString()?.let { input ->
            val differsFromDefault = input.trim() != defaultString
            subject.value = differsFromDefault
            if (differsFromDefault) onSuccess?.invoke(input, this) else onError?.invoke(input, this)
        }
    }
    return subject
}

fun RadioGroup.isChecked(): LiveData<Boolean> {
    val subject = MutableLiveData<Boolean>().apply { value = false }

    setOnCheckedChangeListener { _, checkedId ->
        subject.value = checkedId != -1
    }
    return subject
}
