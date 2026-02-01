package com.example.escolaboaparacachorro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class ConSQL {
    String url = "postgres://avnadmin:AVNS_UjnQfb8Ligw-kHVUcBM@pg-cadastro-germinare-a529.k.aivencloud.com:27765/defaultdb?sslmode=require";
    String user = "avnadmin";
    String password = "AVNS_UjnQfb8Ligw-kHVUcBM";
    Connection conn;

    public String conectar(){
        String msg;
        try {
            conn = DriverManager.getConnection(url, user, password);
            msg = "Conectado com sucesso!!!";
        } catch (SQLException e) {
            e.printStackTrace();
            msg = "Erro ao conectar!!!\n" + "Erro: " + e.getMessage();
        }
        return msg;
    }

}
