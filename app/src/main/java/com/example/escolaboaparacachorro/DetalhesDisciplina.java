package com.example.escolaboaparacachorro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.escolaboaparacachorro.adapter.DetalhesNotasAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentDetalhesDisciplinaBinding;
import com.example.escolaboaparacachorro.model.Notas;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class DetalhesDisciplina extends Fragment {

    private FragmentDetalhesDisciplinaBinding binding;

    private ShapeableImageView fotoDono;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDetalhesDisciplinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView rv = binding.rvNotas;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-lxnr.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiPostgres apiPostgres = retrofit.create(ApiPostgres.class);

        Call<List<Notas>> call = apiPostgres.getNotas();

        call.enqueue(new Callback<List<Notas>>() {
            @Override
            public void onResponse(Call<List<Notas>> call, Response<List<Notas>> response) {
                List<Notas> listaNotas = response.body();
                DetalhesNotasAdapter adapter = new DetalhesNotasAdapter(listaNotas);
                rv.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Notas>> call, Throwable throwable) {
                Toast.makeText(requireContext(), "Erro ao carregra dados.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}






