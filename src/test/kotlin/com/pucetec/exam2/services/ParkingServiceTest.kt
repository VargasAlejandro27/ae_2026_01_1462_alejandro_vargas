package com.pucetec.exam2.services

import com.pucetec.exam2.dto.EntryRequest
import com.pucetec.exam2.dto.ExitRequest
import com.pucetec.exam2.entities.ParkingSpace
import com.pucetec.exam2.entities.Ticket
import com.pucetec.exam2.exceptions.ConflictException
import com.pucetec.exam2.exceptions.NotFoundException
import com.pucetec.exam2.repositories.ParkingSpaceRepository
import com.pucetec.exam2.repositories.TicketRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class ParkingServiceTest {

    private val parkingSpaceRepository: ParkingSpaceRepository = mock()
    private val ticketRepository: TicketRepository = mock()
    private val service = ParkingService(parkingSpaceRepository, ticketRepository)

    @Test
    fun `should return available spaces`() {
        whenever(parkingSpaceRepository.findByOccupiedFalse()).thenReturn(
            listOf(ParkingSpace(id = 1, code = "A1", occupied = false))
        )

        val result = service.findAvailableSpaces()

        assertEquals(1, result.size)
        assertEquals("A1", result[0].code)
    }

    @Test
    fun `should register entry when parking space exists and free`() {
        val space = ParkingSpace(id = 1, code = "A1", occupied = false)
        whenever(parkingSpaceRepository.findById(1)).thenReturn(Optional.of(space))
        whenever(parkingSpaceRepository.findByOccupiedFalse()).thenReturn((1..19).map { ParkingSpace(id = it.toLong(), code = "S$it", occupied = false) })
        whenever(parkingSpaceRepository.save(space)).thenReturn(space)
        whenever(ticketRepository.save(any())).thenAnswer { it.arguments[0] as Ticket }

        val result = service.registerEntry(EntryRequest(plate = "ABC123", parkingSpaceId = 1))

        assertEquals("ABC123", result.plate)
        assertEquals(1, result.parkingSpaceId)
        verify(parkingSpaceRepository, times(1)).save(space)
        verify(ticketRepository, times(1)).save(any())
    }

    @Test
    fun `should throw not found when entry parking space does not exist`() {
        whenever(parkingSpaceRepository.findById(1)).thenReturn(Optional.empty())

        val exception = assertThrows(NotFoundException::class.java) {
            service.registerEntry(EntryRequest(plate = "ABC123", parkingSpaceId = 1))
        }

        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `should throw conflict when entry parking space already occupied`() {
        val occupiedSpace = ParkingSpace(id = 1, code = "A1", occupied = true)
        whenever(parkingSpaceRepository.findById(1)).thenReturn(Optional.of(occupiedSpace))

        val exception = assertThrows(ConflictException::class.java) {
            service.registerEntry(EntryRequest(plate = "ABC123", parkingSpaceId = 1))
        }

        assertTrue(exception.message!!.contains("already occupied"))
    }

    @Test
    fun `should throw conflict when parking is full`() {
        val space = ParkingSpace(id = 1, code = "A1", occupied = false)
        whenever(parkingSpaceRepository.findById(1)).thenReturn(Optional.of(space))
        whenever(parkingSpaceRepository.findByOccupiedFalse()).thenReturn(emptyList())

        val exception = assertThrows(ConflictException::class.java) {
            service.registerEntry(EntryRequest(plate = "ABC123", parkingSpaceId = 1))
        }

        assertTrue(exception.message!!.contains("Parking is full"))
    }

    @Test
    fun `should register exit when ticket exists and open`() {
        val space = ParkingSpace(id = 1, code = "A1", occupied = true)
        val ticket = Ticket(id = 1, plate = "ABC123", entryTime = LocalDateTime.now().minusHours(1), exitTime = null, parkingSpace = space)
        whenever(ticketRepository.findById(1)).thenReturn(Optional.of(ticket))
        whenever(ticketRepository.save(any())).thenAnswer { it.arguments[0] as Ticket }
        whenever(parkingSpaceRepository.save(space)).thenReturn(space)

        val result = service.registerExit(ExitRequest(ticketId = 1))

        assertEquals(1, result.id)
        assertNotNull(result.exitTime)
        assertFalse(space.occupied)
        verify(parkingSpaceRepository, times(1)).save(space)
    }

    @Test
    fun `should throw not found when exit ticket does not exist`() {
        whenever(ticketRepository.findById(1)).thenReturn(Optional.empty())

        val exception = assertThrows(NotFoundException::class.java) {
            service.registerExit(ExitRequest(ticketId = 1))
        }

        assertTrue(exception.message!!.contains("not found"))
    }

    @Test
    fun `should throw conflict when ticket already closed`() {
        val space = ParkingSpace(id = 1, code = "A1", occupied = false)
        val ticket = Ticket(id = 1, plate = "ABC123", entryTime = LocalDateTime.now().minusHours(1), exitTime = LocalDateTime.now(), parkingSpace = space)
        whenever(ticketRepository.findById(1)).thenReturn(Optional.of(ticket))

        val exception = assertThrows(ConflictException::class.java) {
            service.registerExit(ExitRequest(ticketId = 1))
        }

        assertTrue(exception.message!!.contains("already closed"))
    }
}
