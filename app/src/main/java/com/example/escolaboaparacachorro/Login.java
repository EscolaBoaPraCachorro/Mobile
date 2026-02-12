package com.example.escolaboaparacachorro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText senha = findViewById(R.id.senha);
        Button bt_confirmar = findViewById(R.id.confirmar);
        TextView esqueceuASenha = findViewById(R.id.textView4);

        esqueceuASenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, EsqueceuASenha.class));
            }
        });

        bt_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!email.getText().toString().isEmpty()&&!senha.getText().toString().isEmpty()){
                  //adicionar verificação se o user existe no banco de dados
                  Intent intent = new Intent(Login.this, MainActivity.class);
                  intent.putExtra("Email", email.getText().toString());
                  intent.putExtra("Senha", senha.getText().toString());
                  startActivity(intent);
                }
                else{
                    Toast.makeText(Login.this, "preencha todos os campos", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}