package manager.task.common.utils

import com.google.gson.Gson
import manager.task.common.JsonParseException
import java.lang.reflect.Type

object Utils {
    val gson by lazy { Gson() }

    inline fun <reified T> fromJson(src: String, exceptionMsg: String? = null): T {
        try {
            return gson.fromJson(src, T::class.java)
        } catch (e: Throwable) {
            if (exceptionMsg == null) {
                throw JsonParseException("Fail json parse -> ${T::class.simpleName}\r\nsrc:\"$src\"")
            } else {
                throw JsonParseException(exceptionMsg + "\r\nFail json parse -> ${T::class.simpleName}\r\nsrc:\"$src\"")
            }
        }
    }
     fun <T> fromJson(src: String, typeOfT : Type, exceptionMsg: String? = null): T {
        try {
            return gson.fromJson(src, typeOfT)
        } catch (e: Throwable) {
            if (exceptionMsg == null) {
                throw JsonParseException("Fail json parse → ${typeOfT.typeName} from src:\"$src\"", e)
            } else {
                throw JsonParseException(exceptionMsg + "\r\nFail json parse → ${typeOfT.typeName} from src:src:\"$src\"", e)
            }
        }
    }

    fun toJson(src: Any?): String = gson.toJson(src)
}