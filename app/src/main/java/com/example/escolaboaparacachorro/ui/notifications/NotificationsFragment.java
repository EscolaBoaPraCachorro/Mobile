package com.example.escolaboaparacachorro.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.Perfil;
import com.example.escolaboaparacachorro.R;
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

        Long idCachorroLogado = sessionManager.getDogId();


        if (idCachorroLogado == null) {
            Toast.makeText(getContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show();
            return;
        }


        binding.perfil.setOnClickListener(v -> {
            Long dogId = sessionManager.getDogId();
            if (dogId != null ) {
                Bundle args = new Bundle();
                args.putLong("ID_PET", dogId);
                args.putBoolean("MODO_EDICAO", true);

                androidx.navigation.Navigation.findNavController(v)
                        .navigate(R.id.perfilFragment, args);
            } else {
                Toast.makeText(getContext(), "Erro: ID do pet não encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvAumigos.setLayoutManager(new LinearLayoutManager(requireContext()));

        carregarListaAumigos();
        carregarDadosCachorro(idCachorroLogado);
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

            Bundle args = new Bundle();
            args.putLong("ID_PET", cachorro.getId());
            args.putBoolean("MODO_EDICAO", false);
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.perfilFragment, args);
        });

        binding.rvAumigos.setAdapter(adapter);


    }

    private void carregarDadosCachorro(Long id) {
        apiPostgres.getCachorroPorId(id).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (binding == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Cachorro dog = response.body();

                    Glide.with(requireContext())
                            .load(dog.getImagem())
                            .placeholder(R.drawable.cachorro)
                            .into(binding.perfil);

                }
            }

            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {
                Log.e("API_DEBUG", "Falha crítica: " + t.getMessage());
                Toast.makeText(getContext(), "Erro ao conectar com servidor", Toast.LENGTH_SHORT).show();
            }
        });
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