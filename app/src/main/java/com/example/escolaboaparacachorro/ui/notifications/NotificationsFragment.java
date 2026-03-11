package com.example.escolaboaparacachorro.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.escolaboaparacachorro.BaseFragment; // Herda do seu novo Base
import com.example.escolaboaparacachorro.Perfil;
import com.example.escolaboaparacachorro.adapter.AumigosAdapter;
import com.example.escolaboaparacachorro.databinding.FragmentNotificationsBinding;
import com.example.escolaboaparacachorro.model.Cachorro;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ApiPostgres apiPostgres;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiPostgres = RetrofitClient.getInstance();

        binding.perfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Perfil.class);
            intent.putExtra("ID_PET", sessionManager.getDogId());
            intent.putExtra("MODO_EDICAO", true);
            startActivity(intent);
        });


        binding.rvAumigos.setLayoutManager(new LinearLayoutManager(requireContext()));

        carregarListaAumigos();
    }

    private void carregarListaAumigos() {
        apiPostgres.listarCachorros().enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(Call<List<Cachorro>> call, Response<List<Cachorro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    configurarAdapter(response.body());
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<List<Cachorro>> call, Throwable t) {
                showError();
            }
        });
    }

    private void configurarAdapter(List<Cachorro> listaAumigos) {
        AumigosAdapter adapter = new AumigosAdapter(listaAumigos, cachorro -> {
            Intent intent = new Intent(requireContext(), Perfil.class);
            intent.putExtra("ID_PET", String.valueOf(cachorro.getId()));
            intent.putExtra("MODO_EDICAO", false);
            startActivity(intent);
        });

        binding.rvAumigos.setAdapter(adapter);
    }

    private void showError() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar aumigos.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}