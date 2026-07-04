package com.pucetec.exam2.services

import com.pucetec.exam2.dto.EntryRequest
import com.pucetec.exam2.dto.ParkingSpaceDto
import com.pucetec.exam2.dto.TicketDto
import com.pucetec.exam2.dto.ExitRequest
import com.pucetec.exam2.entities.ParkingSpace
import com.pucetec.exam2.entities.Ticket
import com.pucetec.exam2.exceptions.ConflictException
import com.pucetec.exam2.exceptions.NotFoundException
import com.pucetec.exam2.mappers.ParkingSpaceMapper
import com.pucetec.exam2.mappers.TicketMapper
import com.pucetec.exam2.repositories.ParkingSpaceRepository
import com.pucetec.exam2.repositories.TicketRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ParkingService(
    private val parkingSpaceRepository: ParkingSpaceRepository,
    private val ticketRepository: TicketRepository
) {

    private val log = LoggerFactory.getLogger(ParkingService::class.java)
    private val capacidad = 20

    fun findAvailableSpaces(): List<ParkingSpaceDto> {
        val availableSpaces = parkingSpaceRepository.findByOccupiedFalse()
        log.info("Available spaces count={}", availableSpaces.size)
        return availableSpaces.map(ParkingSpaceMapper::toDto)
    }

    @Transactional
    fun registerEntry(entryRequest: EntryRequest): TicketDto {
        val spaceId = entryRequest.parkingSpaceId ?: throw NotFoundException("Parking space id is required")
        val parkingSpace = parkingSpaceRepository.findById(spaceId)
            .orElseThrow { NotFoundException("Parking space with id $spaceId not found") }

        if (parkingSpace.occupied) {
            log.warn("Attempt to enter into occupied space {}", spaceId)
            throw ConflictException("Parking space $spaceId is already occupied")
        }

        val occupiedCount = parkingSpaceRepository.findByOccupiedFalse().let { capacidad - it.size }
        if (occupiedCount >= capacidad) {
            log.warn("Parking full: capacity={} occupied={}", capacidad, occupiedCount)
            throw ConflictException("Parking is full")
        }

        parkingSpace.occupied = true
        val savedSpace = parkingSpaceRepository.save(parkingSpace)
        val ticket = Ticket(
            plate = entryRequest.plate,
            parkingSpace = savedSpace
        )
        val savedTicket = ticketRepository.save(ticket)
        log.info("Created ticket {} for plate {} in space {}", savedTicket.id, entryRequest.plate, savedSpace.id)
        return TicketMapper.toDto(savedTicket)
    }

    @Transactional
    fun registerExit(exitRequest: ExitRequest): TicketDto {
        val ticketId = exitRequest.ticketId ?: throw NotFoundException("Ticket id is required")
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { NotFoundException("Ticket with id $ticketId not found") }

        if (ticket.exitTime != null) {
            log.warn("Attempt to close already closed ticket {}", ticketId)
            throw ConflictException("Ticket $ticketId is already closed")
        }

        ticket.exitTime = LocalDateTime.now()
        val updatedTicket = ticketRepository.save(ticket)
        val parkingSpace = updatedTicket.parkingSpace
        parkingSpace.occupied = false
        parkingSpaceRepository.save(parkingSpace)
        log.info("Closed ticket {} and freed space {}", ticketId, parkingSpace.id)
        return TicketMapper.toDto(updatedTicket)
    }
}
