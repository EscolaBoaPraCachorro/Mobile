package com.example.escolaboaparacachorro.ui.notifications;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.escolaboaparacachorro.Perfil;
import com.example.escolaboaparacachorro.R;
import com.example.escolaboaparacachorro.adapter.AumigosAdapter;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.databinding.FragmentNotificationsBinding;
import com.example.escolaboaparacachorro.model.Cachorro;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ShapeableImageView perfil;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.perfil.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Perfil.class);
            String idDoMeuCachorro = "";
            intent.putExtra("MODO_EDICAO", true); //é o cachorro do usuario
            intent.putExtra("ID_PET", idDoMeuCachorro);

            startActivity(intent);
        });

        RecyclerView rv = binding.rvAumigos;
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-lxnr.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiPostgres apiPostgres = retrofit.create(ApiPostgres.class);

        Call<List<Cachorro>> call = apiPostgres.getCachorros();

        call.enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(Call<List<Cachorro>> call, Response<List<Cachorro>> response) {
                List<Cachorro> listaAumigos = response.body();
                AumigosAdapter adapter = new AumigosAdapter(listaAumigos);
                rv.setAdapter(adapter);



            }

            @Override
            public void onFailure(Call<List<Cachorro>> call, Throwable throwable) {
                Toast.makeText(requireContext(),"Erro ao carregra dados.",Toast.LENGTH_SHORT).show();

            }
        });




    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
