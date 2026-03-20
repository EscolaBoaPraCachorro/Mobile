package com.example.escolaboaparacachorro.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.DetalhesDisciplina;
import com.example.escolaboaparacachorro.R;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentHomeBinding;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.model.Cachorro;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Long idCachorroLogado;
    private ApiPostgres apiPostgres;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        apiPostgres = RetrofitClient.getInstance();

        idCachorroLogado = sessionManager.getDogId();


//        if (idCachorroLogado == null) {
//            Toast.makeText(getContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show();
//            return;
//        }
        binding.perfil3.setOnClickListener(v -> {
            Long dogId = sessionManager.getDogId();
            if (dogId != null ) {
                Bundle args = new Bundle();
                args.putLong("ID_PET", dogId);
                args.putBoolean("MODO_EDICAO", false);

                androidx.navigation.Navigation.findNavController(v)
                        .navigate(R.id.perfilFragment, args);
            } else {
                Toast.makeText(requireContext(), "Erro: ID do pet não encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        carregarDadosCachorro(idCachorroLogado);
        configurarCliquesMaterias();
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
                            .into(binding.perfil3);

                }
            }

            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {
                Log.e("API_DEBUG", "Falha crítica: " + t.getMessage());
              // Toast.makeText(getContext(), "Erro ao conectar com servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarCliquesMaterias() {
        View.OnClickListener materiaClickListener = v -> {
            String nome = null;
            int id = v.getId();

            if (id == R.id.cardObediencia) nome = "Obediência";
            else if (id == R.id.cardSocializacao) nome = "socialização";
            else if (id == R.id.cardAutocontrole) nome = "autocontrole";
            else if (id == R.id.cardTrick) nome = "trick training";
            else if (id == R.id.cardEtiqueta) nome = "etiqueta de passeio";

            if (nome != null) {
                navegarParaDetalhes(nome);
            }
        };

        binding.cardObediencia.setOnClickListener(materiaClickListener);
        binding.cardSocializacao.setOnClickListener(materiaClickListener);
        binding.cardAutocontrole.setOnClickListener(materiaClickListener);
        binding.cardTrick.setOnClickListener(materiaClickListener);
        binding.cardEtiqueta.setOnClickListener(materiaClickListener);
    }

    private void navegarParaDetalhes(String nomeDisciplina) {
        Bundle bundle = new Bundle();
        bundle.putString("nome_disciplina", nomeDisciplina);
        bundle.putLong("id_aluno", idCachorroLogado);

        androidx.navigation.Navigation.findNavController(requireView())
                .navigate(R.id.detalhesDisciplina, bundle);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}