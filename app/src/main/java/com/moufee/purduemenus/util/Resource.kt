package com.moufee.purduemenus.util

sealed class Resource<out R> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    val isSuccess get() = this is Success
    val isLoading get() = this is Loading
    val isError get() = this is Loading
    fun asSuccess() = this as? Success
}