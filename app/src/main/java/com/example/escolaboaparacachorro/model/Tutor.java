package com.example.escolaboaparacachorro.model;

import com.google.gson.annotations.SerializedName;

public class Tutor {
    private Long id;

    private String nome;

    @SerializedName("data_nascimento")
    private String dataNascimento;

    @SerializedName("data_cadastro")
    private String dataCadastro;

    private String genero;

    private String telefone;

    private String cpf;

    private String rg;

    private String email;

    private String imagem;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImagem() {
        if (this.imagem == null || this.imagem.isEmpty()) {
            return "https://i.pinimg.com/1200x/6d/ab/45/6dab45beb83489596815592566a63899.jpg";
        }
        return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }
}
