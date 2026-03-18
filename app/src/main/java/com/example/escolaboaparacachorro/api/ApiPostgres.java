package com.example.escolaboaparacachorro.api;

import com.example.escolaboaparacachorro.model.Cachorro;
import com.example.escolaboaparacachorro.model.Disciplinas;
import com.example.escolaboaparacachorro.model.Notas;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;
import com.example.escolaboaparacachorro.model.Tutor;
import com.example.escolaboaparacachorro.model.request.TutorRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiPostgres {

    // --- Endpoints de Cão ---
    @GET("api/cao/listar")
    Call<List<Cachorro>> listarCachorros();

    @GET("api/cao/buscarCaoPorId/{id}")
    Call<List<Cachorro>> getCachorroPorId(@Path("id") Long idCachorro);

    @GET("api/cao/buscarImagemPorCachorro/{id}")
    Call<Cachorro> getImagemCachorro(@Path("id") Long idCachorro);


    @GET("api/cao/buscarCachorroPorIdTutor/{id}")
    Call<List<Cachorro>> getDadosCachorroPorIdTutor(@Path("id") Long id);


    // --- Endpoints de Tutor ---
    @GET("api/tutor/buscarPorEmail/{email}")
    Call<Tutor> getDadosTutorEmail(@Path("email") String email);
    @GET("api/tutor/buscarPorId/{id}")
    Call<Tutor> getDadosTutorId(@Path("email") Long idTutor);


    // --- Endpoints de Observação ---
    @GET("api/observacao/buscarObservacaoPorIdCachorro/{idCachorro}/Disciplina/{idProfessor}")
    Call<Observacoes> getObservacaoPorAlunoDisciplina(
            @Path("idCachorro") Long idAluno,
            @Path("idProfessor") Long idProfessor
    );


    // --- Endpoints de Notas ---
    @GET("api/notas/buscarNotaPorIdCachorro/{idCachorro}/Disciplina/{idProfessor}")
    Call<List<Notas>> getNotasPorAlunoDisciplina(
            @Path("idCachorro") Long idAluno,
            @Path("idProfessor") Long idProfessor
    );


    // --- Endpoints de Disciplina ---
    @GET("api/disciplina/buscarIdProfessorPorDisciplina/{disciplina}")
    Call<Disciplinas> getIdProfPorDisciplina(@Path("disciplina") String disciplina);


    // --- Endpoints de Professor ---
    @GET("api/professor/buscarImagemProfessorPorId/{id}")
    Call<Professor> getImagemProfPorId(@Path("id") Long id);


    @GET("api/tutor/buscarPorId/{id}")
    Call<Tutor> getTutorPorId(@Path("id") Long id);


    @GET("api/cao/buscarTutorIdPorCachorro/{id}")
    Call<Cachorro> getIdTutorCachorro(@Path("id") Long idCachorro);


    @PATCH("api/tutor/atualizarImagem/{id}")
    Call<Void> atualizarFotoTutor(
            @Path("id") Long id,
            @Body TutorRequest req
    );


    @PATCH("api/tutor/atualizarDescricao/{id}")
    Call<Void> atualizarDescricao(
            @Path("id") Long id,
            @Body TutorRequest req
    );

}