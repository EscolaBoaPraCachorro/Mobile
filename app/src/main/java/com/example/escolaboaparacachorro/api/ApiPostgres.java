package com.example.escolaboaparacachorro.api;

import com.example.escolaboaparacachorro.model.Cachorro;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiPostgres {
    @GET("cachorros")
    Call<List<Cachorro>> getCachorros();
    //exemplo (aguardando api)
}
