/*
 * Copyright (c) 2024. Artem Sukhanov (Stakancheck)
 * All rights reserved.
 *
 * This code is the property of Artem Sukhanov and is a commercial development.
 *
 * For inquiries, please contact:
 * Corporate Email: support@sk-dev.site
 * Personal Email: stakancheck@gmail.com
 */

package skdev.thairestaurant.utils.fields

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


fun List<CoreFormField<*, *>>.validate(): Boolean =
    all { it.validate() }

interface CoreFormField<D, E> {
    fun validate(): Boolean
    fun value(): D
    fun setValue(value: D)
    fun setError(error: E?)
    fun resetValidation()
    fun observeData(lifecycleOwner: LifecycleOwner, onChange: (D) -> Unit): DisposableHandle
    fun observeError(lifecycleOwner: LifecycleOwner, onChange: (E?) -> Unit): DisposableHandle
}

@Stable
class FormField<D, E>(
    scope: CoroutineScope,
    initialValue: D,
    validation: (Flow<D>) -> Flow<E?>
) : BaseFlowFormField<D, E>(scope, initialValue, validation), CoreFormField<D, E> {

    override fun observeData(
        lifecycleOwner: LifecycleOwner,
        onChange: (D) -> Unit
    ): DisposableHandle {
        val job: Job = lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                data.onEach { onChange(it) }.collect()
            }
        }

        return DisposableHandle {
            job.cancel()
        }
    }

    override fun observeError(
        lifecycleOwner: LifecycleOwner,
        onChange: (E?) -> Unit
    ): DisposableHandle {
        val job: Job = lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                error.onEach { onChange(it) }.collect()
            }
        }

        return DisposableHandle {
            job.cancel()
        }
    }
}

class MutableStateAdapter<T>(
    private val state: State<T>,
    private val mutate: (T) -> Unit
) : MutableState<T> {

    override var value: T
        get() = state.value
        set(value) {
            mutate(value)
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

@Composable
fun <T> MutableStateFlow<T>.collectAsMutableState(
    context: CoroutineContext = EmptyCoroutineContext
): MutableState<T> = MutableStateAdapter(
    state = collectAsState(context),
    mutate = { value = it }
)
