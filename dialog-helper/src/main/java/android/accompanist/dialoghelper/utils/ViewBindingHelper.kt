package android.accompanist.dialoghelper.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding

object ViewBindingHelper {

    inline fun <reified T : ViewBinding> inflate(context: Context): Result<T> {
        return runCatching {
            val method = T::class.java.getMethod("inflate", LayoutInflater::class.java)
            val obj = method.invoke(null, LayoutInflater.from(context)) as T
            obj
        }.onFailure { throwable ->
            Log.d("ViewBindingHelper", "inflate: ${throwable.cause}", throwable)
        }
    }

    inline fun <reified T : ViewBinding> bind(view: View): Result<T> {
        return runCatching {
            val obj = T::class.java.getMethod("bind", View::class.java).invoke(null, view) as T
            obj
        }.onFailure { throwable ->
            Log.d("ViewBindingHelper", "bind: ${throwable.cause}", throwable)
        }
    }
}
