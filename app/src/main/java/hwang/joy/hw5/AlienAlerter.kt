package hwang.joy.hw5

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

const val BASE_URL = "http://javadude.com/aliens"

class AlienAlerter {

    @GET("{n}.json")

    suspend fun startReporting() {

    }


    companion object {
        fun create() =
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(AlienAlerter::class.java)
    }

}