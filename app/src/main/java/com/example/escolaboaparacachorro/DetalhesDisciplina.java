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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.adapter.DetalhesNotasAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentDetalhesDisciplinaBinding;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.helpers.SessionManager;
import com.example.escolaboaparacachorro.model.Nota;
import com.example.escolaboaparacachorro.model.Observacoes;
import com.example.escolaboaparacachorro.model.Professor;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalhesDisciplina extends Fragment {

    private FragmentDetalhesDisciplinaBinding binding;
    private ApiPostgres apiPostgres;
    private SessionManager sessionManager;
    private static final String TAG = "API_DEBUG";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalhesDisciplinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiPostgres = RetrofitClient.getInstance();
        sessionManager = new SessionManager(requireContext());

        setupUI();
        processArguments();
    }

    private void setupUI() {
        binding.rvNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.voltar.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void processArguments() {
        Long idAluno = sessionManager.getDogId();
        String nomeDisciplina = getArguments() != null ? getArguments().getString("nome_disciplina") : "";

        if (idAluno == null) {
            Toast.makeText(requireContext(), "Erro: Aluno não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.nomeDisciplina.setText(nomeDisciplina);
        carregarDadosIniciais(nomeDisciplina, idAluno);
    }

    private void carregarDadosIniciais(String disciplina, Long idAluno) {
        apiPostgres.getIdProfPorDisciplina(disciplina).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (binding == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Long idProfessor = response.body();
                    buscarDadosProfessor(idProfessor);
                    buscarNotas(idProfessor, idAluno);
                    buscarObservacoes(idProfessor, idAluno);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                handleFailure("Erro ao buscar ID do professor", t);
            }
        });
    }

    private void buscarDadosProfessor(Long idProfessor) {
        apiPostgres.getProfPorId(idProfessor).enqueue(new Callback<Professor>() {
            @Override
            public void onResponse(@NonNull Call<Professor> call, @NonNull Response<Professor> response) {
                if (binding == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    Professor prof = response.body();

                    Glide.with(requireContext())
                            .load(prof.getImagem())
                            .placeholder(R.drawable.profduble)
                            .into(binding.fotoProfessor);

                    // Nome
                    binding.nomeProf.setText(prof.getNome());

                    // Idade
                    if (prof.getData_nascimento() != null) {
                        String idadeFormatada = calcularIdade(prof.getData_nascimento()) + " anos";
                        binding.idadeProf.setText(idadeFormatada);
                    }
                    Log.e("LOGDEBUG", "tarvei no bsucar nota" );
                }
            }

            @Override
            public void onFailure(@NonNull Call<Professor> call, @NonNull Throwable t) {
                handleFailure("Erro ao buscar dados do professor", t);
            }
        });
    }


    private void buscarNotas(Long idProfessor, Long idAluno) {
        Log.e("LOGDEBUG", "mentira entrei" );
        apiPostgres.getNotasPorAlunoDisciplina(idProfessor, idAluno).enqueue(new Callback<List<Nota>>() {
            @Override
            public void onResponse(@NonNull Call<List<Nota>> call, @NonNull Response<List<Nota>> response) {
                if (binding == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    binding.rvNotas.setAdapter(new DetalhesNotasAdapter(response.body()));
                }
                Log.e("LOGDEBUG", "consegui" );
            }

            @Override
            public void onFailure(@NonNull Call<List<Nota>> call, @NonNull Throwable t) {
                Log.e("LOGDEBUG", "falhei" );
                handleFailure("Erro ao buscar notas", t);
            }
        });
    }

    private void buscarObservacoes(Long idProfessor, Long idAluno) {
        apiPostgres.getObservacaoPorAlunoDisciplina(idAluno, idProfessor).enqueue(new Callback<List<Observacoes>>() {
            @Override
            public void onResponse(@NonNull Call<List<Observacoes>> call, @NonNull Response<List<Observacoes>> response) {
                if (binding == null || !isAdded()) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String descricao = response.body().get(0).getDescricao();
                    Log.d("LOGDEBUG", "Descrição recebida: " + descricao);
                    binding.observacoes.setText(descricao);
                } else {
                    binding.observacoes.setText("Sem observações nesta disciplina.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Observacoes>> call, @NonNull Throwable t) {
                Log.e("LOGDEBUG", "Falha Observação: " + t.getMessage());
                if (binding != null) {
                    binding.observacoes.setText("Erro ao carregar observações.");
                }
            }
        });
    }
    private int calcularIdade(String dataNascimento) {
        try {
            // Espera formato "yyyy-MM-dd" ou similar
            String[] partes = dataNascimento.split("-");
            int anoNasc = Integer.parseInt(partes[0]);
            int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
            return anoAtual - anoNasc;
        } catch (Exception e) {
            return 0;
        }
    }

    private void handleFailure(String message, Throwable t) {
        Log.e(TAG, message + ": " + t.getMessage());
        if (isAdded()) {
            Toast.makeText(getContext(), "Erro ao carregar dados.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}