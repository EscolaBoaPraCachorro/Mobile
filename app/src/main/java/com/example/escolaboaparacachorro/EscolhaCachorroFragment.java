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
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.escolaboaparacachorro.adapter.EscolhaCachorroAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentEscolhaCachorroBinding;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.model.Cachorro;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EscolhaCachorroFragment extends Fragment {

    private FragmentEscolhaCachorroBinding binding;
    private ApiPostgres apiPostgres;
    private SessionManager sessionManager;
    private Long tutorId;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla o layout usando View Binding
        binding = FragmentEscolhaCachorroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa as ferramentas
        apiPostgres = RetrofitClient.getInstance();
        sessionManager = new SessionManager(requireContext());

        // Recupera os dados passados pelo LoginFragment
        if (getArguments() != null) {
            tutorId = getArguments().getLong("tutorId");
            email = getArguments().getString("email");
        }

        // Configura o RecyclerView com 2 colunas (Grid)
        binding.rvcachorros.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        carregarListaCachorros();
    }

    private void carregarListaCachorros() {
        apiPostgres.getDadosCachorroPorIdTutor(tutorId).enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cachorro>> call, @NonNull Response<List<Cachorro>> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    configurarAdapter(response.body());
                } else {
                    showError("Erro ao carregar pets: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Cachorro>> call, @NonNull Throwable t) {
                if (binding == null) return;
                showError("Falha na conexão: " + t.getMessage());
            }
        });
    }

    private void configurarAdapter(List<Cachorro> listaCachorros) {
        EscolhaCachorroAdapter adapter = new EscolhaCachorroAdapter(listaCachorros, cachorro -> {

            sessionManager.createLoginSession(tutorId, email, cachorro.getId());

            Navigation.findNavController(requireView()).navigate(R.id.home);

            Toast.makeText(getContext(), "Perfil de " + cachorro.getNome() + " selecionado!", Toast.LENGTH_SHORT).show();
        });

        binding.rvcachorros.setAdapter(adapter);
    }

    private void showError(String mensagem) {
        if (getContext() != null) {
            Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}