package com.example.escolaboaparacachorro.model;

import com.google.gson.annotations.SerializedName;

public class Disciplinas {

    private Long id;

    @SerializedName("id_professor")
    private Long idProfessor;

    @SerializedName("id_notas")
    private Long idNotas;

    private String nome;

    public Disciplinas() {
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Long idProfessor) {
        this.idProfessor = idProfessor;
    }

    public Long getIdNotas() {
        return idNotas;
    }

    public void setIdNotas(Long idNotas) {
        this.idNotas = idNotas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
