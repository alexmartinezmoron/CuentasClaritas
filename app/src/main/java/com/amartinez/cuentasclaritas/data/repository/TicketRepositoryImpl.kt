package com.amartinez.cuentasclaritas.data.repository

import com.amartinez.cuentasclaritas.data.database.dao.TicketDao
import com.amartinez.cuentasclaritas.data.database.entities.TicketEntity
import com.amartinez.cuentasclaritas.domain.repository.TicketRepository
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao
) : TicketRepository {
    override suspend fun insertTicket(ticket: TicketEntity): Long {
        return ticketDao.insertTicket(ticket)
    }
}

