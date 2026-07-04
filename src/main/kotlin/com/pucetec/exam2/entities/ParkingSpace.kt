package com.pucetec.exam2.entities

import jakarta.persistence.*

@Entity
@Table(name = "parking_spaces")
class ParkingSpace(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val code: String,

    @Column(nullable = false)
    var occupied: Boolean = false
)
