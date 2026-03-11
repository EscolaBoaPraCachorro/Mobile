package com.example.escolaboaparacachorro.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.escolaboaparacachorro.Perfil;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.adapter.AumigosAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentNotificationsBinding;
import com.example.escolaboaparacachorro.model.Cachorro;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ApiPostgres apiPostgres;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiPostgres = RetrofitClient.getInstance();
        sessionManager = new SessionManager(requireContext());

        binding.perfil.setOnClickListener(v -> {
            String dogId = sessionManager.getDogId();
            if (dogId != null) {
                Intent intent = new Intent(requireContext(), Perfil.class);
                intent.putExtra("ID_PET", dogId);
                intent.putExtra("MODO_EDICAO", true);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Erro: ID do pet não encontrado na sessão", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvAumigos.setLayoutManager(new LinearLayoutManager(requireContext()));

        carregarListaAumigos();
    }

    private void carregarListaAumigos() {
        apiPostgres.listarCachorros().enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cachorro>> call, @NonNull Response<List<Cachorro>> response) {
                if (binding == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    configurarAdapter(response.body());
                } else {
                    showError("Erro do servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Cachorro>> call, @NonNull Throwable t) {
                if (binding == null) return;
                showError("Falha na rede: " + t.getMessage());
            }
        });
    }

    private void configurarAdapter(List<Cachorro> listaAumigos) {
        AumigosAdapter adapter = new AumigosAdapter(listaAumigos, cachorro -> {
            // Ao clicar em um aumigo da lista, abre o perfil em modo visualização
            Intent intent = new Intent(requireContext(), Perfil.class);
            intent.putExtra("ID_PET", String.valueOf(cachorro.getId()));
            intent.putExtra("MODO_EDICAO", false);
            startActivity(intent);
        });

        binding.rvAumigos.setAdapter(adapter);
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