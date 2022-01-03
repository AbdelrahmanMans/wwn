package android.mohamed.worldwidenews.utils
//wrapper class to make code more readable
sealed class NetworkResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResponse<T>(data)
    class Error<T>(message: String, data: T? = null) :
        NetworkResponse<T>(message = message, data = data)

    class Loading<T> : NetworkResponse<T>()
    class Initialized<T> : NetworkResponse<T>()
}