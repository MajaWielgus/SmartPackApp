package com.example.smartpack;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    // URL API OpenWeather (zastąp {API_KEY} swoim kluczem)
    String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @GET("weather")
    Call<WeatherResponse> getWeather(@Query("q") String cityName,
                                     @Query("appid") String apiKey,
                                     @Query("units") String units);
}
