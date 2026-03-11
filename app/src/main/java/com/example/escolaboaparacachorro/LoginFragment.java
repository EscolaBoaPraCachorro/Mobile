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

import com.example.escolaboaparacachorro.databinding.FragmentEsqueceuSenhaBinding;
import com.example.escolaboaparacachorro.databinding.FragmentLoginBinding;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.ui.home.HomeFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding; // Usaremos o binding para facilitar
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializa o View Binding
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        sessionManager = new SessionManager(requireContext());
        mAuth = FirebaseAuth.getInstance();

        // Se já estiver logado, vai direto para a Home
        if (sessionManager.isLoggedIn()) {
            irParaTelaPrincipal();
        }

        // Navegação para "Esqueceu Senha"
        binding.textView4.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.loginFragment, new EsqueceuSenha()) // Use o ID do container da Activity
                    .addToBackStack(null)
                    .commit();
        });

        // Botão Confirmar
        binding.confirmar.setOnClickListener(v -> {
            String emailTxt = binding.email.getText().toString().trim();
            String senhaTxt = binding.senha.getText().toString().trim();

            if (!emailTxt.isEmpty() && !senhaTxt.isEmpty()) {
                fazerLoginComFirebase(emailTxt, senhaTxt);
            } else {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void fazerLoginComFirebase(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sessionManager.createLoginSession(user.getUid(), email, "dog_default_id");

                            irParaTelaPrincipal();
                        }
                    } else {
                        Toast.makeText(getContext(), "Erro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irParaTelaPrincipal() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.loginFragment, new HomeFragment()) // Use o ID do container da Activity
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}