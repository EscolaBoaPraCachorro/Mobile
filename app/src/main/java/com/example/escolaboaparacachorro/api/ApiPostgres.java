package com.example.escolaboaparacachorro.api;

import com.example.escolaboaparacachorro.model.Cachorro;
import com.example.escolaboaparacachorro.model.Disciplinas;
import com.example.escolaboaparacachorro.model.Notas;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;
import com.example.escolaboaparacachorro.model.Tutor;
import com.example.escolaboaparacachorro.model.request.LoginRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPostgres {

    @GET("/listar")
    Call<List<Cachorro>> listarCachorros();

    @GET("/buscarCaoPorId/{id}")
    Call<List<Cachorro>> getCachorroPorId(@Path("id") Long idCachorro);

    @GET("/buscarImagemPorCachorro/{id}")
    Call<Cachorro> getImagemCachorro(@Path("id") Long idCachorro);

    @GET("/buscarObservacaoPorIdCachorro/{idCachorro}/Disciplina/{idProfessor}")
    Call<Observacoes> getObservacaoPorAlunoDisciplina(
            @Path("idCachorro") Long idAluno,
            @Path("idProfessor") Long idProfessor
    );


    @GET("/buscarNotaPorIdCachorro/{idCachorro}/Disciplina/{idProfessor}")
    Call<List<Notas>> getNotasPorAlunoDisciplina(
            @Path("idCachorro") Long idAluno,
            @Path("idProfessor") Long idProfessor
    );

    @GET("getIdTutorCachorro/{id}")
    Call<Cachorro> getIdTutorCachorro(@Path("id") Long idCachorro);

    @GET("/buscarIdProfessorPorDisciplina/{disciplina}")
    Call<Disciplinas> getIdProfPorDisciplina(@Path("disciplina") String disciplina);

    @GET("/buscarImagemProfessorPorId/{id}")
    Call<Professor> getImagemProfPorId(@Path("id") Long id);

    @GET("/buscarDadosTutorPorEmail/{email}")
    Call<Tutor> getDadosTutorEmail(@Path("email") String email);

    @GET("/buscarDadosCachorroPorIdTutor/{id}")
    Call<Cachorro> getDadosCachorroPorIdTutor(@Path("id") Long ig);


    @FormUrlEncoded
    @POST("/atualizar_descricao.php")
    Call<Void> atualizarDescricao(
            @Field("id_pet") Long idPet,
            @Field("descricao") String descricao
    );

    @FormUrlEncoded
    @POST("/atualizar_foto.php")
    Call<Void> atualizarFoto(
            @Field("id_pet") Long idPet,
            @Field("url_foto") String urlFoto
    );
}