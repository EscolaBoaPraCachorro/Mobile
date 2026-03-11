package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        sessionManager = new SessionManager(requireContext());

        if (sessionManager.isLoggedIn()) {
            irParaTelaPrincipal();
        }


        TextInputEditText email = view.findViewById(R.id.email);
        TextInputEditText senha = view.findViewById(R.id.senha);
        Button bt_confirmar = view.findViewById(R.id.confirmar);
        TextView esqueceuASenha = view.findViewById(R.id.textView4);

        esqueceuASenha.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new EsqueceuASenhaFragment())
                    .addToBackStack(null)
                    .commit();
        });

        bt_confirmar.setOnClickListener(v -> {
            String emailTxt = email.getText().toString().trim();
            String senhaTxt = senha.getText().toString().trim();

            if (!emailTxt.isEmpty() && !senhaTxt.isEmpty()) {
                fazerLoginPelaApi(emailTxt, senhaTxt);
            } else {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private void fazerLoginComFirebase(String email, String senha) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();

                            sessionManager.createLoginSession(userId, email);

                            irParaTelaPrincipal();
                        }
                    } else {
                        Toast.makeText(getContext(), "Erro no Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irParaTelaPrincipal() {
        HomeFragment proximoFragmento = new HomeFragment();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, proximoFragmento)
                .commit();
    }
}