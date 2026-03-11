package com.example.escolaboaparacachorro;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.escolaboaparacachorro.ui.home.HomeFragment;

public class SplashScreen extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla o layout que era da sua Activity
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeFragment proximoFragmento = new HomeFragment();
        // Handler para esperar os 10 segundos (ajustado para o padrão moderno)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded()) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.home_fragment, proximoFragmento)
                        .addToBackStack(null)
                        .commit();
            }
        }, 10000);
    }
}