package com.example.escolaboaparacachorro.model;

import com.google.gson.annotations.SerializedName;

public class Notas {

    private Long id;
    @SerializedName("id_cachorro")
    private Long idCachorro;
    @SerializedName("id_professor")
    private Long idProfessor;
    private Integer nota;
    @SerializedName("data_publicacao")
    private String dataPublicacao;
    public Notas(Integer nota) {
        this.nota = nota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public Long getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Long idProfessor) {
        this.idProfessor = idProfessor;
    }

    public Long getIdCachorro() {
        return idCachorro;
    }

    public void setIdCachorro(Long idCachorro) {
        this.idCachorro = idCachorro;
    }
}
