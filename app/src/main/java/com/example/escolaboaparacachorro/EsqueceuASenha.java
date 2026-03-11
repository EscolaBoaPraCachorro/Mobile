package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.escolaboaparacachorro.databinding.FragmentEsqueceuSenhaBinding;
import com.google.firebase.auth.FirebaseAuth;

public class EsqueceuSenhaFragment extends BaseFragment {

    private FragmentEsqueceuSenhaBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEsqueceuSenhaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding.btnEnviarEmail.setOnClickListener(v -> {
            String email = binding.editEmailEsqueceu.getText().toString().trim();

            if (email.isEmpty()) {
                binding.editEmailEsqueceu.setError("Digite seu e-mail");
                return;
            }

            enviarEmailRecuperacao(email);
        });

        binding.imageView4.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void enviarEmailRecuperacao(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "E-mail de recuperação enviado para: " + email,
                                Toast.LENGTH_LONG).show();


                        getParentFragmentManager().popBackStack();
                    } else {
                        String erro = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                        Toast.makeText(getContext(), "Erro: " + erro, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}