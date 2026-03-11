package com.example.escolaboaparacachorro;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.databinding.FragmentPerfilBinding;
import com.example.escolaboaparacachorro.model.Cachorro;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends Fragment {

    private FragmentPerfilBinding binding;
    private String idPet;
    private boolean modoEdicao;
    private ApiPostgres apiPostgres;
    private FirebaseStorage storage;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiPostgres = RetrofitClient.getInstance();
        storage = FirebaseStorage.getInstance();
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            idPet = getArguments().getString("ID_PET");
            modoEdicao = getArguments().getBoolean("MODO_EDICAO", false);
        }

        configurarInterface();
        carregarDadosDoPerfil();

        binding.editImg.setOnClickListener(v -> tirarFoto());
        binding.editDescr.setOnClickListener(v -> salvarDescricao());
    }

    private void configurarInterface() {
        if (modoEdicao) {
            binding.viewFinder.setVisibility(View.VISIBLE);
            binding.editImg.setVisibility(View.VISIBLE);
            binding.descricao.setEnabled(true);
            binding.editDescr.setVisibility(View.VISIBLE);
            iniciarCameraX();
        } else {
            binding.viewFinder.setVisibility(View.GONE);
            binding.editImg.setVisibility(View.GONE);
            binding.descricao.setEnabled(false);
            binding.editDescr.setVisibility(View.GONE);
        }
    }

    private void iniciarCameraX() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(requireActivity().getWindowManager().getDefaultDisplay().getRotation())
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
            } catch (Exception e) {
                Log.e("CameraX", "Erro ao iniciar", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void tirarFoto() {
        if (imageCapture == null) return;

        File file = new File(requireContext().getExternalFilesDir(null), idPet + ".jpg");
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(options, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults results) {
                Uri uri = Uri.fromFile(file);
                binding.imageView10.setImageURI(uri);
                fazerUploadFirebase(uri);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exc) {
                Toast.makeText(getContext(), "Erro ao capturar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fazerUploadFirebase(Uri uri) {
        Toast.makeText(getContext(), "Fazendo upload da foto...", Toast.LENGTH_SHORT).show();
        StorageReference ref = storage.getReference().child("fotos_pets/" + idPet + ".jpg");

        ref.putFile(uri).addOnSuccessListener(task -> {
            ref.getDownloadUrl().addOnSuccessListener(url -> {
                salvarLinkNoPostgres(url.toString());
            });
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Falha no Firebase", Toast.LENGTH_SHORT).show());
    }

    private void salvarLinkNoPostgres(String url) {
        apiPostgres.atualizarFoto(idPet, url).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) Toast.makeText(getContext(), "Foto atualizada!", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void salvarDescricao() {
        String novaDescricao = binding.descricao.getText().toString();
        apiPostgres.atualizarDescricao(idPet, novaDescricao).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) Toast.makeText(getContext(), "Descrição salva!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Erro ao salvar descrição", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDadosDoPerfil() {
        apiPostgres.getCachorroPorId(idPet).enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(Call<List<Cachorro>> call, Response<List<Cachorro>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    preencherDadosCachorro(response.body().get(0));
                }
            }
            @Override
            public void onFailure(Call<List<Cachorro>> call, Throwable t) {
                Log.e("API_ERROR", "Erro ao carregar pet", t);
            }
        });

        apiPostgres.getIdTutorCachorro(idPet).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cachorro tutor = response.body();
                    binding.textView17.setText(tutor.getNome());
                    binding.idade.setText(calcularIdade(tutor.getDataNascimento()) + " anos");
                    Glide.with(requireContext()).load(tutor.getImagem()).circleCrop().into(binding.imageView12);
                }
            }
            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {}
        });
    }

    private void preencherDadosCachorro(Cachorro cachorro) {
        binding.nome.setText(cachorro.getNome());
        binding.raca.setText(cachorro.getRaca());
       // binding.descricao.setText(cachorro.getDescricao());
        Glide.with(requireContext())
                .load(cachorro.getImagem())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imageView10);
    }

    private String calcularIdade(String dataNascString) {
        if (dataNascString == null || dataNascString.isEmpty()) return "0";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date dataNasc = sdf.parse(dataNascString);
            Calendar nasc = Calendar.getInstance();
            nasc.setTime(dataNasc);
            Calendar hoje = Calendar.getInstance();
            int idade = hoje.get(Calendar.YEAR) - nasc.get(Calendar.YEAR);
            if (hoje.get(Calendar.DAY_OF_YEAR) < nasc.get(Calendar.DAY_OF_YEAR)) idade--;
            return String.valueOf(Math.max(idade, 0));
        } catch (ParseException e) { return "0"; }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraExecutor != null) cameraExecutor.shutdown();
        binding = null;
    }
}