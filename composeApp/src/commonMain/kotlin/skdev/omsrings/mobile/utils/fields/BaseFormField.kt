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

package skdev.omsrings.mobile.utils.fields

import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn


@Stable
abstract class BaseFlowFormField<D, E>(
    scope: CoroutineScope,
    initialValue: D,
    validation: (Flow<D>) -> Flow<E?>
) : CoreFormField<D, E> {

    val data: MutableStateFlow<D> =
        MutableStateFlow(initialValue)

    private val validationError: MutableStateFlow<E?> =
        MutableStateFlow(null)

    private val showValidationError: MutableStateFlow<Boolean> =
        MutableStateFlow(false)

    val error: StateFlow<E?> =
        combine(validationError, showValidationError) { error, show ->
            if (show) error else null
        }.stateIn(scope, SharingStarted.Eagerly, null)

    val isValid: StateFlow<Boolean> =
        validationError.map { it == null }
            .stateIn(scope, SharingStarted.Eagerly, true)

    init {
        validation(data)
            .onEach { validationError.value = it }
            .launchIn(scope)

        data
            .onEach {
                resetValidation()
            }
            .launchIn(scope)
    }

    override fun setValue(value: D) {
        data.value = value
    }

    override fun setError(error: E?) {
        validationError.value = error
    }

    override fun value(): D {
        return data.value
    }

    override fun validate(): Boolean {
        showValidationError.value = true
        return isValid.value
    }

    override fun resetValidation() {
        showValidationError.value = false
    }
}

fun <D, E> flowBlock(block: (D) -> E?): ((Flow<D>) -> Flow<E?>) {
    return { flow ->
        flow.map { block(it) }
    }
}

fun validateAll(vararg fields: FormField<*, *>): Boolean =
    fields.map { it.validate() }.reduce { v1, v2 -> v1 && v2 }
