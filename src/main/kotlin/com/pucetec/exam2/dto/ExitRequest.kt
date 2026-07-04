package com.pucetec.exam2.dto

import jakarta.validation.constraints.NotNull

data class ExitRequest(
    @field:NotNull
    val ticketId: Long?
)
