package com.example.escolaboaparacachorro.api;

import com.example.escolaboaparacachorro.model.Cachorro;
import com.example.escolaboaparacachorro.model.Disciplinas;
import com.example.escolaboaparacachorro.model.Notas;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;
import com.example.escolaboaparacachorro.model.Tutor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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

    @GET("api/cao/getIdTutorCachorro/{id}")
    Call<Cachorro> getIdTutorCachorro(@Path("id") Long idCachorro);

    @GET("api/cao/buscarCachorroPorIdTutor/{id}")
    Call<List<Cachorro>> getDadosCachorroPorIdTutor(@Path("id") Long id);


    // --- Endpoints de Tutor ---
    @GET("api/tutor/buscarPorEmail/{email}")
    Call<Tutor> getDadosTutorEmail(@Path("email") String email);


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


    // --- Endpoints de Atualização (PHP ou Legado) ---
    // Nota: Verifique se esses arquivos PHP também ficam dentro de /api/cao/
    @FormUrlEncoded
    @POST("api/cao/atualizar_descricao.php")
    Call<Void> atualizarDescricao(
            @Field("id_pet") Long idPet,
            @Field("descricao") String descricao
    );

    @FormUrlEncoded
    @POST("api/cao/atualizar_foto.php")
    Call<Void> atualizarFoto(
            @Field("id_pet") Long idPet,
            @Field("url_foto") String urlFoto
    );
}