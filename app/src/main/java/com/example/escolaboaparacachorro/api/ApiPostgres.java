package com.example.escolaboaparacachorro.api;

import com.example.escolaboaparacachorro.model.Cachorro;
import com.example.escolaboaparacachorro.model.Disciplinas;
import com.example.escolaboaparacachorro.model.Notas;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;
import com.example.escolaboaparacachorro.model.Tutor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiPostgres {

    @GET("/listar")
    Call<List<Cachorro>> listarCachorros();
    @GET("/buscarCaoPorId/{id}")
    Call<List<Cachorro>> getCachorroPorId(@Path("id_cachorro") String idCachorro);


    @GET("/buscarImagemPorCachorro/{id}")
    Call<Cachorro> getImagemCachorro(@Path("id") String idCachorro);

    @GET("/buscarObservacaoPorIdCachorro/{idCachorro}/Disciplina/{idProfessor}")
    Call<Observacoes> getObservacaoPorAlunoDisciplina(@Query("disciplina") String nomeDisciplina,@Path("id_cachorro") String idAluno);
    @GET("/buscarNotaPorIdCachorro/{idCachorro}/Disciplina/{idProfessor}")
    Call <List<Notas>> getNotasPorAlunoDisciplina(@Path("id_professor") String idProfessor, @Path("id_cachorro") String idAluno);

    @GET("getIdTutorCachorro/{id}") // busca o id do tutor pelo id do cachorro
    Call<Cachorro> getIdTutorCachorro(@Path("id") String idCachorro);
    @GET("/buscarIdProfessorPorDisciplina/{disciplina}")
    Call<Disciplinas> getIdProfPorDisciplina(@Path("disciplina") String disciplina);
    @GET("/buscarImagemProfessorPorId/{id}")
    Call<Professor> getImagemProfPorId(@Path("id") String id);


    @GET("/buscarNomeProfessorPorId/{id}")
    Call<Professor> getNomeProfPorId(@Path("id") String id);
    @GET("/buscarDataNacimentoProfessorPorId/{id}")
    Call<Professor> getDataNacimentoProfPorId(@Path("id") String id);

    @GET("/buscarImagemPorId/{id}")
    Call<Tutor> getFotoTutorPorId(@Path("id") String id);
    @GET("/buscarNomeTutorPorId/{id}")
    Call<Tutor> getDataNomeTutorPorId(@Path("id") String id);
    @GET("/buscarDataNacimentoTutorPorId/{id}")
    Call<Tutor> getDataNacimentoTutorPorId(@Path("id") String id);

    @GET("/buscarImagemPorCachorro/{id}")
    Call<Cachorro> getImagemCachorroId( @Path("id_cachorro") String idCachorro);
    @GET("/buscarDataNacimentoPorCachorro/{id}")
    Call<Cachorro> getDataNascimentoCachorroId( @Path("id_cachorro") String idCachorro);
    @GET("/buscarNomePorCachorro/{id}")
    Call<Cachorro> getNomeCachorroId( @Path("id_cachorro") String idCachorro);
    @GET("/buscarTurmaPorCachorro/{id}")
    Call<Cachorro> getTurmaCachorroId( @Path("id_cachorro") String idCachorro);

    @GET("/buscarNotaPorIdCachorro/{id}")
    Call<List<Notas>> getNotas();













}
