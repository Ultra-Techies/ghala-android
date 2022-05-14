package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.deliverynotes.CreateDeliveryNoteResponse
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DeliveryNotesApi {

    @POST("api/deliveryNote")
    suspend fun createDeliveryNotes(
        @Body createDeliveryNote: CreateDeliveryNoteRequest
    ): CreateDeliveryNoteResponse

    @GET("api/deliverynotes/all")
    suspend fun getAllDeliveryNotes(): List<FetchDeliveryNotesResponseItem>
}