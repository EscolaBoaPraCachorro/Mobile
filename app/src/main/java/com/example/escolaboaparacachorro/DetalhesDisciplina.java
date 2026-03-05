package com.example.escolaboaparacachorro;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.adapter.DetalhesNotasAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentDetalhesDisciplinaBinding;
import com.example.escolaboaparacachorro.model.Disciplinas;
import com.example.escolaboaparacachorro.model.Notas;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DetalhesDisciplina extends Fragment {

    private FragmentDetalhesDisciplinaBinding binding;
    private Retrofit retrofit;
    private ApiPostgres apiPostgres;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalhesDisciplinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-lxnr.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiPostgres = retrofit.create(ApiPostgres.class);

        String nomeDisciplina = "";
        String id_aluno = "";
        // PEGAR ID CACHORRO POR SHARED PREFERENCES

        //idCachorroLogado = PreferencesHelper.getIdAluno(requireContext());

        // if (idCachorroLogado.isEmpty()) {
        //     Toast.makeText(getContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show();
        //      return;
        //  }

        if (getArguments() != null) {
            nomeDisciplina = getArguments().getString("nome_disciplina");
            id_aluno = getArguments().getString("id_aluno");
            binding.nomeDisciplina.setText(nomeDisciplina);
        }

        binding.rvNotas.setLayoutManager(new LinearLayoutManager(requireContext()));

        buscarDadosProfessorENotas(nomeDisciplina, id_aluno);
        buscarObservacoes(nomeDisciplina, id_aluno);

        binding.voltar.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void buscarDadosProfessorENotas(String disciplina, String id_aluno) {
        apiPostgres.getIdProfPorDisciplina(disciplina).enqueue(new Callback<Disciplinas>() {
            @Override
            public void onResponse(Call<Disciplinas> call, Response<Disciplinas> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long id_professor = response.body().getIdProfessor();

                    carregarFotoProfessor(String.valueOf(id_professor));
                    carregarNotas(String.valueOf(id_professor), id_aluno);
                }
            }

            @Override
            public void onFailure(Call<Disciplinas> call, Throwable t) {
                showError();
            }
        });
    }

    private void carregarFotoProfessor(String id_professor) {
        apiPostgres.getImagemProfPorId(id_professor).enqueue(new Callback<Professor>() {
            @Override
            public void onResponse(Call<Professor> call, Response<Professor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String urlImagem = response.body().getImagem();
                    Glide.with(requireContext())
                            .load(urlImagem)
                            .into(binding.fotoProfessor);

                }
            }
            @Override
            public void onFailure(Call<Professor> call, Throwable t) {}
        });
    }

    private void carregarNotas(String id_professor, String id_aluno) {
        apiPostgres.getNotasPorAlunoDisciplina(id_professor, id_aluno).enqueue(new Callback<List<Notas>>() {
            @Override
            public void onResponse(Call<List<Notas>> call, Response<List<Notas>> response) {
                if (response.body() != null) {
                    DetalhesNotasAdapter adapter = new DetalhesNotasAdapter(response.body());
                    binding.rvNotas.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Notas>> call, Throwable t) {
                showError();
            }
        });
    }

    private void buscarObservacoes(String disciplina, String id_aluno) {
        apiPostgres.getObservacaoPorAlunoDisciplina(disciplina, id_aluno).enqueue(new Callback<Observacoes>() {
            @Override
            public void onResponse(Call<Observacoes> call, Response<Observacoes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.observacoes.setText(response.body().getDescricao());
                }
            }
            @Override
            public void onFailure(Call<Observacoes> call, Throwable t) {
                showError();
            }
        });
    }

    private void showError() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar dados.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}






