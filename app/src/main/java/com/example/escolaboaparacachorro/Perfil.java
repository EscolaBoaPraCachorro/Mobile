package com.example.escolaboaparacachorro;

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

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentPerfilBinding;
import com.example.escolaboaparacachorro.model.Cachorro;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Perfil extends Fragment {

    private FragmentPerfilBinding binding;
    private String id_aluno = "";
    private ApiPostgres apiPostgres;
    private Retrofit retrofit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // PEGAR ID CACHORRO POR SHARED PREFERENCES
        // id_aluno = PreferencesHelper.getIdAluno(requireContext());
        id_aluno = "123";

        if (id_aluno.isEmpty()) {
            Toast.makeText(getContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = getActivity().getIntent();
        boolean eDono = intent.getBooleanExtra("MODO_EDICAO", false);
        configurarLayout(eDono);

        carregarDadosDoPerfil();
    }

    private void carregarDadosDoPerfil() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-lxnr.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiPostgres = retrofit.create(ApiPostgres.class);

        apiPostgres.getCachorroPorId(id_aluno).enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(Call<List<Cachorro>> call, Response<List<Cachorro>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Cachorro cachorro = response.body().get(0);
                    preencherDadosCachorro(cachorro);
                }
            }
            @Override
            public void onFailure(Call<List<Cachorro>> call, Throwable t) {
                Log.e("API_ERROR", "Erro ao carregar cachorro", t);
            }
        });

        apiPostgres.getImagemCachorro(id_aluno).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Glide.with(requireContext())
                            .load(response.body().getImagem())
                            .into(binding.imageView10);
                }
            }
            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {}
        });

        apiPostgres.getIdTutorCachorro(id_aluno).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cachorro tutor = response.body();
                    binding.textView17.setText(tutor.getNome());

                    // Transformando a String de data em idade numerp
                    String idadeTutor = calcularIdade(tutor.getDataNascimento());
                    binding.idadeT.setText(idadeTutor);

                    Glide.with(requireContext())
                            .load(tutor.getImagem())
                            .circleCrop()
                            .into(binding.imageView12);
                }
            }
            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {}
        });
    }

    private void preencherDadosCachorro(Cachorro cachorro) {
        binding.nome.setText(cachorro.getNome());


        binding.idade.setText(calcularIdade(cachorro.getDataNascimento()) + "anos");

        binding.turma.setText(cachorro.getTurma());
        binding.sexo.setText(cachorro.getSexo());
        binding.raca.setText(cachorro.getRaca());
    }

    private String calcularIdade(String dataNascimentoString) {
        if (dataNascimentoString == null || dataNascimentoString.isEmpty()) return "0";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date dataNasc = sdf.parse(dataNascimentoString);
            Calendar dataNascimento = Calendar.getInstance();
            dataNascimento.setTime(dataNasc);

            Calendar hoje = Calendar.getInstance();

            int idade = hoje.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);

            if (hoje.get(Calendar.DAY_OF_YEAR) < dataNascimento.get(Calendar.DAY_OF_YEAR)) {
                idade--;
            }

            return String.valueOf(idade);
        } catch (ParseException e) {
            Log.e("DATA_ERROR", "Erro ao converter data: " + dataNascimentoString);
            return "0";
        }
    }

    private void configurarLayout(boolean eDono) {
        int visibilidade = eDono ? View.VISIBLE : View.GONE;
        binding.editImg.setVisibility(visibilidade);
        binding.editDescr.setVisibility(visibilidade);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}