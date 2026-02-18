package com.example.escolaboaparacachorro;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.escolaboaparacachorro.databinding.FragmentPerfilBinding;


public class Perfil extends Fragment {

    private FragmentPerfilBinding binding;
    private ImageView btnEditFoto;
    private ImageView btnEditTexto;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnEditFoto = binding.editImg;
        btnEditTexto = binding.editDescr;

        Intent intent = getActivity().getIntent();

        boolean eDono = intent.getBooleanExtra("MODO_EDICAO", false);
        String idPet = intent.getStringExtra("ID_PET");


        configurarLayout(eDono);

    }

    private void configurarLayout(boolean eDono) {
        if (eDono) {
            btnEditFoto.setVisibility(View.VISIBLE);
            btnEditTexto.setVisibility(View.VISIBLE);

        } else {
            btnEditFoto.setVisibility(View.GONE);
            btnEditTexto.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}