package com.moufee.purduemenus.repository

sealed class RepoStatus<T> {
    data class Success<T>(val data: T) : RepoStatus<T>()
    class Loading<T> : RepoStatus<T>()
    data class Error<T>(val error: Throwable, val data: T?) : RepoStatus<T>()
}