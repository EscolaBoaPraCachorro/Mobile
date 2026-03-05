package com.example.escolaboaparacachorro.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.DetalhesDisciplina;
import com.example.escolaboaparacachorro.R;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentHomeBinding;
import com.example.escolaboaparacachorro.model.Cachorro;
import com.google.android.material.imageview.ShapeableImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ShapeableImageView perfil;
    private ApiPostgres apiPostgres;
    private String idCachorroLogado;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idCachorroLogado = "";

        //PEGAR ID CACHORRO POR PREFERENCES

      //  idCachorroLogado = PreferencesHelper.getIdAluno(requireContext());

       // if (idCachorroLogado.isEmpty()) {
       //     Toast.makeText(getContext(), "Usuário não identificado", Toast.LENGTH_SHORT).show();
      //      return;
      //  }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-lxnr.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiPostgres = retrofit.create(ApiPostgres.class);
        carregarImagemCachorro(idCachorroLogado);

        //listener para as materias
        View.OnClickListener materiaClickListener = v -> {
            String nome = "";

            if (v.getId() == R.id.cardObediencia) nome = "Obediência";
            else if (v.getId() == R.id.cardSocializacao) nome = "Socialização";
            else if (v.getId() == R.id.cardAutocontrole) nome = "Autocontrole";
            else if (v.getId() == R.id.cardTrick) nome = "Trick";
            else if (v.getId() == R.id.cardEtiqueta) nome = "Etiqueta";


            if (!nome.isEmpty()) navegarParaDetalhes(nome);
        };


        binding.cardObediencia.setOnClickListener(materiaClickListener);
        binding.cardSocializacao.setOnClickListener(materiaClickListener);
        binding.cardAutocontrole.setOnClickListener(materiaClickListener);
        binding.cardTrick.setOnClickListener(materiaClickListener);
        binding.cardEtiqueta.setOnClickListener(materiaClickListener);



    }

    private void carregarImagemCachorro(String id) {
        apiPostgres.getImagemCachorro(id).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String urlImagem = response.body().getImagem();
                    Glide.with(requireContext())
                            .load(urlImagem)
                            .into(binding.perfil3);
                }
            }

            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao carregar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navegarParaDetalhes(String nomeDisciplina) {
        Bundle bundle = new Bundle();
        bundle.putString("nome_disciplina", nomeDisciplina);
        bundle.putString("id_aluno", idCachorroLogado);


        DetalhesDisciplina proximoFragmento = new DetalhesDisciplina();
        proximoFragmento.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.home_fragment, proximoFragmento)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}