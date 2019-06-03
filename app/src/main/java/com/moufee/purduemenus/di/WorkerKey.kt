package com.moufee.purduemenus.di

import androidx.work.Worker
import dagger.MapKey
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class WorkerKey(val value: KClass<out Worker>)