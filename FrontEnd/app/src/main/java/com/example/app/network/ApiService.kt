package network

import com.example.app.models.Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {


    @GET("cities/{cityId}/events")
    fun getEventsForCity(@Path("cityId") cityId: String): Call<List<Event>>

    @GET("events/byId/{eventId}")
    fun getEventDetails(@Path("eventId") eventId: String): Call<Event>

    @GET("events/byCategory/{category}")
    fun getEventsByCategory(@Path("category") category: String): Call<List<Event>>

    @PUT("events/{eventId}")
    fun updateEventTickets(
        @Path("eventId") eventId: String,
        @Body remainingTickets: Map<String, Int>
    ): Call<Event>
}
