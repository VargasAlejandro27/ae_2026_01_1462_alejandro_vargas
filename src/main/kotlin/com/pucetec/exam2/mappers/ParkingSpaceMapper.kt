package com.pucetec.exam2.mappers

import com.pucetec.exam2.dto.ParkingSpaceDto
import com.pucetec.exam2.entities.ParkingSpace

object ParkingSpaceMapper {
    fun toDto(entity: ParkingSpace): ParkingSpaceDto =
        ParkingSpaceDto(
            id = entity.id ?: 0,
            code = entity.code,
            occupied = entity.occupied
        )
}
