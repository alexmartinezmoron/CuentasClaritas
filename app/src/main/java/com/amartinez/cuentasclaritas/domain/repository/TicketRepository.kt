package com.amartinez.cuentasclaritas.domain.repository

import com.amartinez.cuentasclaritas.data.database.entities.TicketEntity

interface TicketRepository {
    suspend fun insertTicket(ticket: TicketEntity): Long
}

