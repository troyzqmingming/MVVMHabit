package me.cya.retrofit.utils

import me.cya.retrofit.exception.ExceptionHandler
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

object RxStreamHelper {
    /**
     * 统一调度
     */
    fun <T> io2Main(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(Schedulers.io())
                .onErrorResumeNext(HttpResponseFunc())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private class HttpResponseFunc<T> : Function<Throwable, Observable<T>> {
        override fun apply(t: Throwable): Observable<T> {
            return Observable.error(ExceptionHandler.handleException(t))
        }
    }
}