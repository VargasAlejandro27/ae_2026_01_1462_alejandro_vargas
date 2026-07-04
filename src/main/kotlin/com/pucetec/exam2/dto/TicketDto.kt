package com.pucetec.exam2.dto

import java.time.LocalDateTime

data class TicketDto(
    val id: Long,
    val plate: String,
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime?,
    val parkingSpaceId: Long,
    val parkingSpaceCode: String
)
