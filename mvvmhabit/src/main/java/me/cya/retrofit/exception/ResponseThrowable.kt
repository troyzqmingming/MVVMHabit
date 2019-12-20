package me.cya.retrofit.exception

import java.lang.Exception

class ResponseThrowable(throwable: Throwable, val errorCode: Int, val httpErrorCode: Int? = null) :
    Exception(throwable) {
    var errorMsg: String = ""
}