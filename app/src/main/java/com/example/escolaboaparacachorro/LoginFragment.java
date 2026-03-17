package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
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
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null && sessionManager.isLoggedIn()) {
            irParaHome();
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.confirmar.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String senha = binding.senha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inicia o Login
            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DEBUG_LOGIN", "1. Firebase OK. Buscando tutor...");
                    verificarCachorrosDoTutor(email);
                } else {
                    Log.e("DEBUG_LOGIN", "Erro Firebase: " + task.getException().getMessage());

                    Toast.makeText(getContext(), "E-mail ou senha inválidos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void verificarCachorrosDoTutor(String email) {
        RetrofitClient.getInstance().getDadosTutorEmail(email).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(@NonNull Call<Tutor> call, @NonNull Response<Tutor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DEBUG_LOGIN", "2. Tutor encontrado: " + response.body().getId());
                    buscarListaDeCachorros(response.body().getId(), email);
                } else {
                    Log.e("DEBUG_LOGIN", "Erro API Tutor: Código " + response.code());
                    Toast.makeText(getContext(), "Erro ao buscar dados do tutor", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Tutor> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Erro de conexão" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarListaDeCachorros(Long tutorId, String email) {
        Log.d("DEBUG_LOGIN", "CHEGOU NA BSUCA CACHORRO " );
        RetrofitClient.getInstance().getDadosCachorroPorIdTutor(tutorId).enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cachorro>> call, @NonNull Response<List<Cachorro>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.d("DEBUG_LOGIN", "3. Lista de cachorros: " + response.body().size());
                    List<Cachorro> lista = response.body();

                    if (lista.isEmpty()) {
                        Toast.makeText(getContext(), "Nenhum pet encontrado para este tutor", Toast.LENGTH_LONG).show();
                    } else if (lista.size() == 1) {
                        sessionManager.createLoginSession(tutorId, email, lista.get(0).getId());
                        irParaHome();
                    } else {

                        Bundle bundle = new Bundle();
                        bundle.putLong("tutorId", tutorId);
                        bundle.putString("email", email);
                        Navigation.findNavController(requireView()).navigate(R.id.escolhaCachorro, bundle);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Cachorro>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Falha ao carregar pets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void irParaHome() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build();

        Navigation.findNavController(requireView()).navigate(R.id.home, null, navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}