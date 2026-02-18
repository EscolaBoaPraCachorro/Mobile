package com.example.escolaboaparacachorro.model;

import com.google.gson.annotations.SerializedName;

public class Cachorro {
    private Integer id;
    private String nome;

    @SerializedName("data_nascimento")
    private String dataNascimento;

    @SerializedName("tutor_id")
    private Integer tutorId;

    private String turma;

    @SerializedName("data_cadastro")
    private String dataCadastro;

    private String sexo;
    private String raca;
    private boolean ativo;

    @SerializedName("tem_pedigree")
    private boolean temPedigree;

    private String rga;

    @SerializedName("sin_patinhas")
    private String sinPatinhas;

    private String imagem;

    public Cachorro() {
    }

    // Getter e Setter

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }



    public String getImagem () {
        if (this.imagem == null || this.imagem.isEmpty()) {
            return "https://i.pinimg.com/1200x/4d/14/89/4d1489adc39294dcc6307f007456f135.jpg";
        }
        return imagem;
    }

    public void setImagem(String imagem){
        this.imagem= imagem;
    }
}