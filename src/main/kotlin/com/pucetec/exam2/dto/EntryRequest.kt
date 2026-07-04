package com.pucetec.exam2.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class EntryRequest(
    @field:NotBlank
    val plate: String,

    @field:NotNull
    val parkingSpaceId: Long?
)
