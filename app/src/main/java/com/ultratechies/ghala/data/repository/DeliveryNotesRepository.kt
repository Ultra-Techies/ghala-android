package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.deliverynotes.CreateDeliveryNoteResponse
import javax.inject.Inject

interface DeliveryNotesRepository {
    suspend fun createDeliveryNotes(createDeliveryNotes: CreateDeliveryNoteRequest): APIResource<CreateDeliveryNoteResponse>
}

class DeliveryNotesRepositoryImpl @Inject constructor(private val deliveryNotesApi: DeliveryNotesApi) :
    DeliveryNotesRepository, BaseRepo() {
    override suspend fun createDeliveryNotes(createDeliveryNotes: CreateDeliveryNoteRequest) =
        safeApiCall {
            deliveryNotesApi.createDeliveryNotes(createDeliveryNotes)
        }
}