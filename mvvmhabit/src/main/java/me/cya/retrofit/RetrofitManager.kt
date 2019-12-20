package me.cya.retrofit

import me.cya.retrofit.callback.BaseRequestCallback
import me.cya.retrofit.callback.BaseResponseCallback
import me.cya.retrofit.exception.ResponseThrowable
import me.cya.retrofit.utils.RxStreamHelper
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class RetrofitManager private constructor(
    private val baseUrl: String,
    private val headers: MutableMap<String, String>
) {

    private constructor(builder: Builder) : this(
        builder.baseUrl,
        builder.header
    )

    companion object {
        fun build(init: Builder.() -> Unit) = Builder(init).build()
    }

    /**
     * builder模式
     */
    class Builder private constructor() {
        internal lateinit var baseUrl: String
        internal var header: MutableMap<String, String> = mutableMapOf()

        constructor(init: Builder.() -> Unit) : this() {
            init()
        }

        fun baseUrl(init: Builder.() -> String) = apply { baseUrl = init() }
        fun header(init: Builder.() -> MutableMap<String, String>) = apply { header = init() }
        fun build() = RetrofitManager(this)
    }

    interface Request<T, S> : BaseRequestCallback {
        fun doRequest(serviceClass: S): Observable<T>
    }


    fun <T, S> doRequest(
            serviceClass: Class<S>,
            request: Request<T, S>,
            response: BaseResponseCallback<T>
    ): Disposable {
        val service = RetrofitClient(baseUrl = baseUrl, headers = headers).create(serviceClass)
        return request.doRequest(service)
            .compose(RxStreamHelper.io2Main())
            .doOnSubscribe {
                request.doOnSubscribe()
            }
            .subscribe({
                response.onResponseSuccess(it)
            }, {
                if (it is ResponseThrowable) {
                    response.onResponseError(it)
                }
            })
    }

}