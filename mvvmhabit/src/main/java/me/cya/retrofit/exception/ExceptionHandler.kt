package me.cya.retrofit.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

object ExceptionHandler {

    private const val UNAUTHORIZED = 401
    private const val FORBIDDEN = 403
    private const val NOT_FOUND = 404
    private const val REQUEST_TIMEOUT = 408
    private const val INTERNAL_SERVER_ERROR = 500
    private const val SERVICE_UNAVAILABLE = 503

    fun handleException(e: Throwable): ResponseThrowable {
        val ex: ResponseThrowable
        when (e) {
            is HttpException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_HTTP, e.code())
                when (e.code()) {
                    UNAUTHORIZED -> ex.errorMsg = "操作未授权"
                    FORBIDDEN -> ex.errorMsg = "请求被拒绝"
                    NOT_FOUND -> ex.errorMsg = "资源不存在"
                    REQUEST_TIMEOUT -> ex.errorMsg = "服务器执行超时"
                    INTERNAL_SERVER_ERROR -> ex.errorMsg = "服务器内部错误"
                    SERVICE_UNAVAILABLE -> ex.errorMsg = "服务器不可用"
                    else -> ex.errorMsg = "网络错误"
                }
            }
            is JsonParseException,
            is JSONException,
            is ParseException,
            is MalformedJsonException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_PARSE)
                ex.errorMsg = "解析错误"
            }

            is ConnectException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_NETWORK_CONNECT)
                ex.errorMsg = "连接失败"
            }

            is javax.net.ssl.SSLException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_SSL)
                ex.errorMsg = "证书验证失败"
            }
            is ConnectTimeoutException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_TIMEOUT)
                ex.errorMsg = "连接超时"
            }
            is java.net.SocketTimeoutException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_TIMEOUT)
                ex.errorMsg = "连接超时"
            }
            is java.net.UnknownHostException -> {
                ex = ResponseThrowable(e, ERROR.ERROR_TIMEOUT)
                ex.errorMsg = "主机地址未知"
            }
            else -> {
                ex = ResponseThrowable(e, ERROR.ERROR_UNKNOWN)
                ex.errorMsg = "未知错误"
            }
        }
        return ex
    }


    /**
     * 约定异常
     */
    internal object ERROR {
        /**
         * 未知错误
         */
        const val ERROR_UNKNOWN = 1000
        /**
         * 解析错误
         */
        const val ERROR_PARSE = 1001
        /**
         * 网络错误
         */
        const val ERROR_NETWORK_CONNECT = 1002
        /**
         * 协议出错
         */
        const val ERROR_HTTP = 1003

        /**
         * 证书出错
         */
        const val ERROR_SSL = 1005

        /**
         * 连接超时
         */
        const val ERROR_TIMEOUT = 1006
    }

}