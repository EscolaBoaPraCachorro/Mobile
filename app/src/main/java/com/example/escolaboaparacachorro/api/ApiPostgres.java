package com.example.escolaboaparacachorro.api;

import com.example.escolaboaparacachorro.model.Cachorro;
import com.example.escolaboaparacachorro.model.Notas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiPostgres {
    @GET("cachorros")
    Call<List<Cachorro>> getCachorros();
    //exemplo (aguardando api)

    @GET("notas")
    Call<List<Notas>> getNotas();
    //exemplo (aguardando api)
}
