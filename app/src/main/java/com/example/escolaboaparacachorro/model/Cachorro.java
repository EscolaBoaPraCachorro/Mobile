package com.example.escolaboaparacachorro.model;

import com.google.gson.annotations.SerializedName;

public class Cachorro {
    private Long id;
    private String nome;

    @SerializedName("data_nascimento")
    private String dataNascimento;

    @SerializedName("tutor_id")
    private Long tutorId;

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

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public String getTurma() {
        return turma;
    }

    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isTemPedigree() {
        return temPedigree;
    }

    public void setTemPedigree(boolean temPedigree) {
        this.temPedigree = temPedigree;
    }

    public String getRga() {
        return rga;
    }

    public void setRga(String rga) {
        this.rga = rga;
    }

    public String getSinPatinhas() {
        return sinPatinhas;
    }

    public void setSinPatinhas(String sinPatinhas) {
        this.sinPatinhas = sinPatinhas;
    }
}