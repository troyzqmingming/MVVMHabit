package me.cya.retrofit.callback

import me.cya.retrofit.exception.ResponseThrowable

interface BaseResponseCallback<T> {

    fun onResponseSuccess(result: T)

    fun onResponseError(exception: ResponseThrowable)

}