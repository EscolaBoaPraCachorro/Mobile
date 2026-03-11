package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.adapter.DetalhesNotasAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.databinding.FragmentDetalhesDisciplinaBinding;
import com.example.escolaboaparacachorro.model.Disciplinas;
import com.example.escolaboaparacachorro.model.Notas;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalhesDisciplina extends Fragment {

    private FragmentDetalhesDisciplinaBinding binding;
    private ApiPostgres apiPostgres;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalhesDisciplinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicialização
        apiPostgres = RetrofitClient.getInstance();
        sessionManager = new SessionManager(requireContext());

        String idAluno = sessionManager.getDogId();
        String nomeDisciplina = "";

        if (getArguments() != null) {
            nomeDisciplina = getArguments().getString("nome_disciplina");
            binding.nomeDisciplina.setText(nomeDisciplina);
        }

        if (idAluno == null ) {
            Toast.makeText(requireContext(), "Erro: Aluno não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.rvNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.voltar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        carregarDadosIniciais(nomeDisciplina, idAluno);
    }

    private void carregarDadosIniciais(String disciplina, String idAluno) {
        apiPostgres.getIdProfPorDisciplina(disciplina).enqueue(new Callback<Disciplinas>() {
            @Override
            public void onResponse(@NonNull Call<Disciplinas> call, @NonNull Response<Disciplinas> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    String idProfessor = String.valueOf(response.body().getIdProfessor());
                    buscarFotoProfessor(idProfessor);
                    buscarNotas(idProfessor, idAluno);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Disciplinas> call, @NonNull Throwable t) {
                showError();
            }
        });

        buscarObservacoes(disciplina, idAluno);
    }

    private void buscarFotoProfessor(String idProfessor) {
        apiPostgres.getImagemProfPorId(idProfessor).enqueue(new Callback<Professor>() {
            @Override
            public void onResponse(@NonNull Call<Professor> call, @NonNull Response<Professor> response) {
                if (binding == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Glide.with(requireContext())
                            .load(response.body().getImagem())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(binding.fotoProfessor);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Professor> call, @NonNull Throwable t) {}
        });
    }

    private void buscarNotas(String idProfessor, String idAluno) {
        apiPostgres.getNotasPorAlunoDisciplina(idProfessor, idAluno).enqueue(new Callback<List<Notas>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notas>> call, @NonNull Response<List<Notas>> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    binding.rvNotas.setAdapter(new DetalhesNotasAdapter(response.body()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Notas>> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void buscarObservacoes(String disciplina, String idAluno) {
        apiPostgres.getObservacaoPorAlunoDisciplina(disciplina, idAluno).enqueue(new Callback<Observacoes>() {
            @Override
            public void onResponse(@NonNull Call<Observacoes> call, @NonNull Response<Observacoes> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    binding.observacoes.setText(response.body().getDescricao());
                } else {
                    binding.observacoes.setText("Sem observações para esta disciplina.");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Observacoes> call, @NonNull Throwable t) {
                showError();
            }
        });
    }

    private void showError() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar dados da disciplina.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}