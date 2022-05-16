package com.ultratechies.ghala.data.repository

import com.ultratechies.ghala.data.models.AppDatasource
import com.ultratechies.ghala.data.models.requests.deliverynotes.CreateDeliveryNoteRequest
import com.ultratechies.ghala.data.models.responses.deliverynotes.CreateDeliveryNoteResponse
import com.ultratechies.ghala.data.models.responses.deliverynotes.FetchDeliveryNotesResponseItem
import kotlinx.coroutines.flow.first
import javax.inject.Inject

interface DeliveryNotesRepository {
    suspend fun createDeliveryNotes(createDeliveryNotes: CreateDeliveryNoteRequest): APIResource<CreateDeliveryNoteResponse>
    suspend fun fetchDeliveryNotes(): APIResource<List<FetchDeliveryNotesResponseItem>>
    suspend fun changeDeliveryNoteStatus(id: Int,state: Int): APIResource<FetchDeliveryNotesResponseItem>
}

class DeliveryNotesRepositoryImpl @Inject constructor(private val deliveryNotesApi: DeliveryNotesApi,private val appDatasource: AppDatasource) :
    DeliveryNotesRepository, BaseRepo() {
    override suspend fun createDeliveryNotes(createDeliveryNotes: CreateDeliveryNoteRequest) =
        safeApiCall {
            deliveryNotesApi.createDeliveryNotes(createDeliveryNotes)
        }
    override suspend fun fetchDeliveryNotes() = safeApiCall {
        val user = appDatasource.getUserFromPreferencesStore().first()
        deliveryNotesApi.getAllDeliveryNotes(user?.assignedWarehouse)
    }

    override suspend fun changeDeliveryNoteStatus(id: Int, state:Int) = safeApiCall{
       deliveryNotesApi.changeDeliveryNoteStatus(id,state)
    }


}