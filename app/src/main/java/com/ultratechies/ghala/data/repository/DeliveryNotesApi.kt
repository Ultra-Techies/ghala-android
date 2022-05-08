package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.deliverynotes.CreateDeliveryNoteResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DeliveryNotesApi {

    @POST("deliveryNote")
    suspend fun createDeliveryNotes(
        @Body createDeliveryNote: CreateDeliveryNoteRequest
    ): CreateDeliveryNoteResponse

}