package com.pucetec.exam2.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tickets")
class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val plate: String,

    @Column(nullable = false)
    val entryTime: LocalDateTime = LocalDateTime.now(),

    @Column
    var exitTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_space_id", nullable = false)
    val parkingSpace: ParkingSpace
)
