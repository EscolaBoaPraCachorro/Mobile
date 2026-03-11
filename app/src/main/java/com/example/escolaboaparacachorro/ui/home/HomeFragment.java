package com.example.escolaboaparacachorro.ui.home;

import android.os.Bundle;
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
    private String idCachorroLogado;
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


        if (idCachorroLogado == null) {
            Toast.makeText(getContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        carregarDadosCachorro(idCachorroLogado);


        configurarCliquesMaterias();
    }

    private void carregarDadosCachorro(String id) {

        apiPostgres.getImagemCachorro(id).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Atualiza a foto e o nome se quiser
                    Glide.with(requireContext())
                            .load(response.body().getImagem())
                            .into(binding.perfil3);
                }
            }

            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao conectar com servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarCliquesMaterias() {
        View.OnClickListener materiaClickListener = v -> {
            String nome = null;
            int id = v.getId();

            if (id == R.id.cardObediencia) nome = "Obediência";
            else if (id == R.id.cardSocializacao) nome = "Socialização";
            else if (id == R.id.cardAutocontrole) nome = "Autocontrole";
            else if (id == R.id.cardTrick) nome = "Trick";
            else if (id == R.id.cardEtiqueta) nome = "Etiqueta";

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
        bundle.putString("id_aluno", idCachorroLogado);

        DetalhesDisciplina proximoFragmento = new DetalhesDisciplina();
        proximoFragmento.setArguments(bundle);


        getParentFragmentManager().beginTransaction()
                .replace(R.id.home, proximoFragmento)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}