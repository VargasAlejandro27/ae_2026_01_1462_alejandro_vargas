package com.pucetec.exam2.mappers

import com.pucetec.exam2.dto.TicketDto
import com.pucetec.exam2.entities.Ticket

object TicketMapper {
    fun toDto(entity: Ticket): TicketDto =
        TicketDto(
            id = entity.id ?: 0,
            plate = entity.plate,
            entryTime = entity.entryTime,
            exitTime = entity.exitTime,
            parkingSpaceId = entity.parkingSpace.id ?: 0,
            parkingSpaceCode = entity.parkingSpace.code
        )
}
