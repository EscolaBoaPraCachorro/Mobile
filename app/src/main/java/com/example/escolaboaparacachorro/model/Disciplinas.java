package com.example.escolaboaparacachorro.model;

import com.google.gson.annotations.SerializedName;

public class Disciplinas {

    private Integer id;

    @SerializedName("id_cachorro")
    private Integer idCachorro;

    @SerializedName("id_notas")
    private Integer idNotas;

    private String nome;

    public Disciplinas() {
    }

    // Getter e Setter

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdCachorro() {
        return idCachorro;
    }

    public void setIdCachorro(Integer idCachorro) {
        this.idCachorro = idCachorro;
    }

    public Integer getIdNotas() {
        return idNotas;
    }

    public void setIdNotas(Integer idNotas) {
        this.idNotas = idNotas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
