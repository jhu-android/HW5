package hwang.joy.hw5

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "http://javadude.com/aliens"

interface AlienApiService {
    @GET("{n}.json")
    suspend fun getPosition(@Path("n") n: String): Response<AlienAlert>

    companion object {
        fun create(): AlienApiService =
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(AlienApiService::class.java)
    }


}