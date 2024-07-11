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

package skdev.omsrings.mobile.utils.fields.validators

import org.jetbrains.compose.resources.StringResource

fun ValidationResult<String>.notEmpty(errorText: StringResource) = nextValidation { value ->
    if (value.isNotEmpty()) {
        ValidationResult.success(value)
    } else {
        ValidationResult.failure(errorText)
    }
}

fun ValidationResult<String>.notBlank(errorText: StringResource) = nextValidation { value ->
    if (value.isNotBlank()) {
        ValidationResult.success(value)
    } else {
        ValidationResult.failure(errorText)
    }
}

fun ValidationResult<String>.minLength(errorText: StringResource, minLength: Int = 0) =
    nextValidation { value ->
        if (value.length < minLength) {
            ValidationResult.failure(errorText)
        } else {
            ValidationResult.success(value)
        }
    }

fun ValidationResult<String>.maxLength(errorText: StringResource, maxLength: Int = 0) =
    nextValidation { value ->
        if (value.length > maxLength) {
            ValidationResult.failure(errorText)
        } else {
            ValidationResult.success(value)
        }
    }

fun ValidationResult<String>.matchRegex(errorText: StringResource, regex: Regex, canBeBlank: Boolean = false) =
    nextValidation { value ->
        if (canBeBlank && value.isBlank()) {
            ValidationResult.success(value)
        } else {
            if (regex.matches(value)) {
                ValidationResult.success(value)
            } else {
                ValidationResult.failure(errorText)
            }
        }
    }

fun ValidationResult<String>.containedIn(errorText: StringResource, validValues: List<String>) =
    nextValidation { value ->
        if (validValues.contains(value)) {
            ValidationResult.success(value)
        } else {
            ValidationResult.failure(errorText)
        }
    }

inline fun <reified D> ValidationResult<D?>.notNull(errorText: StringResource) =
    nextValidation { value ->
        if (value != null) {
            ValidationResult.success(value)
        } else {
            ValidationResult.failure(errorText)
        }
    }
