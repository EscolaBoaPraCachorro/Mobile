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
import com.example.escolaboaparacachorro.model.Tutor;
import com.example.escolaboaparacachorro.model.request.TutorRequest;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Perfil extends Fragment {

    private FragmentPerfilBinding binding;
    private Long idPet;
    private Long idTutor;
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

        binding.voltarPerfil.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigateUp()
        );

        if (getArguments() != null) {
            idPet = getArguments().getLong("ID_PET");
            modoEdicao = getArguments().getBoolean("MODO_EDICAO", false);
            Log.d("PERFIL_DEBUG", "ID Pet Recebido: " + idPet);
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
            binding.descricao.setFocusableInTouchMode(true);
            binding.editDescr.setVisibility(View.VISIBLE);
            iniciarCameraX();
        } else {
            binding.viewFinder.setVisibility(View.GONE);
            binding.editImg.setVisibility(View.GONE);
            binding.descricao.setFocusable(false);
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

        // Se estiver editando o Tutor, a foto pode ser salva com o idTutor
        String fileName = (idTutor != null) ? "tutor_" + idTutor : "pet_" + idPet;
        File file = new File(requireContext().getExternalFilesDir(null), fileName + ".jpg");
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
                if(isAdded()) Toast.makeText(getContext(), "Erro ao capturar foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fazerUploadFirebase(Uri uri) {
        if (idTutor == null) return;
        if(isAdded()) Toast.makeText(getContext(), "Fazendo upload da foto...", Toast.LENGTH_SHORT).show();

        StorageReference ref = storage.getReference().child("fotos_tutores/" + idTutor + ".jpg");

        ref.putFile(uri).addOnSuccessListener(task -> {
            ref.getDownloadUrl().addOnSuccessListener(url -> {
                salvarLinkNoPostgres(url.toString());
            });
        }).addOnFailureListener(e -> {
            if(isAdded()) Toast.makeText(getContext(), "Falha no Firebase", Toast.LENGTH_SHORT).show();
        });
    }

    private void salvarLinkNoPostgres(String url) {
        TutorRequest request = new TutorRequest();
        request.setImagem(url);

        apiPostgres.atualizarFotoTutor(idTutor, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Foto do perfil atualizada!", Toast.LENGTH_SHORT).show();

                        Glide.with(Perfil.this)
                                .load(url)
                                .circleCrop()
                                .into(binding.imageView12);
                    }
                } else {
                    Log.e("API_ERROR", "Erro ao atualizar: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Falha na conexão ao salvar link da foto", t);
            }
        });
    }

    private void salvarDescricao() {
        if (idTutor == null) {
            if (isAdded()) Toast.makeText(getContext(), "Aguarde carregar dados do tutor", Toast.LENGTH_SHORT).show();
            return;
        }

        String novaDescricao = binding.descricao.getText().toString();

        TutorRequest request = new TutorRequest();
        request.setDescricao(novaDescricao);

        apiPostgres.atualizarDescricao(idTutor, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Descrição do tutor salva!", Toast.LENGTH_SHORT).show();
                        Log.d("API_SUCCESS", "Tutor " + idTutor + " atualizado.");
                    }
                } else {
                    Log.e("API_ERROR", "Código de erro: " + response.code());
                    if (isAdded()) Toast.makeText(getContext(), "Erro ao salvar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("API_FAILURE", "Falha na conexão: " + t.getMessage());
                if (isAdded()) Toast.makeText(getContext(), "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDadosDoPerfil() {
        if (idPet == null) return;

        apiPostgres.getCachorroPorId(idPet).enqueue(new Callback<List<Cachorro>>() {
            @Override
            public void onResponse(@NonNull Call<List<Cachorro>> call, @NonNull Response<List<Cachorro>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    preencherDadosCachorro(response.body().get(0));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Cachorro>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erro ao carregar pet", t);
            }
        });

        apiPostgres.getIdTutorCachorro(idPet).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(@NonNull Call<Cachorro> call, @NonNull Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Aqui pegamos o ID do tutor que veio dentro do objeto Cachorro
                    idTutor = response.body().getId();

                    if (idTutor != null) {
                        buscarInformacoesDoTutor(idTutor);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Cachorro> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erro ao localizar ID do tutor", t);
            }
        });
    }

    private void buscarInformacoesDoTutor(Long id) {
        apiPostgres.getTutorPorId(id).enqueue(new Callback<Tutor>() {
            @Override
            public void onResponse(@NonNull Call<Tutor> call, @NonNull Response<Tutor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Tutor tutor = response.body();

                    // Preenche os campos do tutor na tela
                    binding.textView17.setText(tutor.getNome());
                    binding.descricao.setText(tutor.getDescricao());
                    binding.idade.setText(calcularIdade(tutor.getDataNascimento()) + " anos");

                    if (isAdded()) {
                        Glide.with(Perfil.this)
                                .load(tutor.getImagem())
                                .circleCrop()
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(binding.imageView12);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tutor> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erro ao carregar dados detalhados do tutor", t);
            }
        });
    }

    private void preencherDadosCachorro(Cachorro cachorro) {
        binding.nome.setText(cachorro.getNome());
        binding.raca.setText(cachorro.getRaca());
        binding.sexo.setText(cachorro.getSexo());
        binding.turma.setText(cachorro.getTurma());

        if (isAdded()) {
            Glide.with(Perfil.this)
                    .load(cachorro.getImagem())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.imageView10);
        }
    }
    private String calcularIdade(String dataNascString) {
        if (dataNascString == null || dataNascString.isEmpty()) return "0";
        String format = dataNascString.contains("-") ? "yyyy-MM-dd" : "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date dataNasc = sdf.parse(dataNascString);
            Calendar nasc = Calendar.getInstance();
            nasc.setTime(dataNasc);
            Calendar hoje = Calendar.getInstance();
            int idade = hoje.get(Calendar.YEAR) - nasc.get(Calendar.YEAR);
            if (hoje.get(Calendar.DAY_OF_YEAR) < nasc.get(Calendar.DAY_OF_YEAR)) idade--;
            return String.valueOf(Math.max(idade, 0));
        } catch (ParseException e) {
            return "0";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraExecutor != null) cameraExecutor.shutdown();
        binding = null;
    }
}