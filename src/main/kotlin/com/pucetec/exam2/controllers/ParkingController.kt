package com.pucetec.exam2.controllers

import com.pucetec.exam2.dto.EntryRequest
import com.pucetec.exam2.dto.ExitRequest
import com.pucetec.exam2.services.ParkingService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/parking")
class ParkingController(
    private val parkingService: ParkingService
) {

    @GetMapping("/available")
    fun getAvailableSpaces() = parkingService.findAvailableSpaces()

    @PostMapping("/entry")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerEntry(@RequestBody @Valid request: EntryRequest) = parkingService.registerEntry(request)

    @PostMapping("/exit")
    @PreAuthorize("isAuthenticated()")
    fun registerExit(@RequestBody @Valid request: ExitRequest) = parkingService.registerExit(request)
}
