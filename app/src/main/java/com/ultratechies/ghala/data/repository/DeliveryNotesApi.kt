package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.deliverynotes.CreateDeliveryNoteResponse
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import retrofit2.http.*

interface DeliveryNotesApi {

    @POST("api/deliverynotes")
    suspend fun createDeliveryNotes(
        @Body createDeliveryNote: CreateDeliveryNoteRequest
    ): CreateDeliveryNoteResponse

    @GET("api/deliverynotes/wh/{id}")
    suspend fun getAllDeliveryNotes(@Path("id") id: Int?): List<FetchDeliveryNotesResponseItem>

    @PUT("api/deliverynotes/{id}/{status}")
    suspend fun changeDeliveryNoteStatus( @Path("id") id: Int,@Path("status") status: Int): FetchDeliveryNotesResponseItem
}