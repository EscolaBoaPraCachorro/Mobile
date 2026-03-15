package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.escolaboaparacachorro.databinding.FragmentLoginBinding;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.model.Cachorro;
import com.example.escolaboaparacachorro.model.Tutor;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.confirmar.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String senha = binding.senha.getText().toString().trim();
            if (!email.isEmpty() && !senha.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        verificarCachorrosDoTutor(email);
                    } else {
                        Toast.makeText(getContext(), "Erro de login", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void verificarCachorrosDoTutor(String email) {
        RetrofitClient.getInstance().getDadosTutorEmail(email).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(@NonNull Call<Tutor> call, @NonNull Response<Tutor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long tutorId = response.body().getId();
                    buscarListaDeCachorros(tutorId, email);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Tutor> call, @NonNull Throwable t) { /* Toast */ }
        });
    }

    private void buscarListaDeCachorros(Long tutorId, String email) {
        // Usando o endpoint que retorna a LISTA de cachorros do tutor
        RetrofitClient.getInstance().getDogsPorTutor(tutorId).enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cachorro>> call, @NonNull Response<List<Cachorro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cachorro> lista = response.body();

                    if (lista.size() == 1) {
                        // CASO 1: Apenas um cachorro -> Vai direto
                        sessionManager.createLoginSession(tutorId, email, lista.get(0).getId());
                        Navigation.findNavController(requireView()).navigate(R.id.home);
                    } else if (lista.size() > 1) {
                        // CASO 2: Vários cachorros -> Vai para tela de escolha
                        Bundle bundle = new Bundle();
                        bundle.putLong("tutorId", tutorId);
                        bundle.putString("email", email);
                        Navigation.findNavController(requireView()).navigate(R.id.escolhaCachorro, bundle);
                    } else {
                        Toast.makeText(getContext(), "Nenhum pet cadastrado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Cachorro>> call, @NonNull Throwable t) { /* Toast */ }
        });
    }
}