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
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.escolaboaparacachorro.api.ApiPostgres;
import com.example.escolaboaparacachorro.helpers.RetrofitClient;
import com.example.escolaboaparacachorro.databinding.FragmentPerfilBinding;
import com.example.escolaboaparacachorro.helpers.SessionManager;
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
    private SessionManager sessionManager;

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
        sessionManager = new SessionManager(requireContext());
        binding.voltarPerfil.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(v).navigateUp()
        );
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sessionManager.logout();
            Navigation.findNavController(requireView())
                    .navigate(R.id.loginFragment);

            Toast.makeText(requireContext(), "Até logo!", Toast.LENGTH_SHORT).show();
        });


        if (getArguments() != null) {
            idPet = getArguments().getLong("ID_PET");
            modoEdicao = getArguments().getBoolean("MODO_EDICAO", false);
            Log.d("PERFIL_DEBUG", "ID Pet Recebido: " + idPet);
        }

       configurarInterface();
        carregarDadosDoPerfil();

       // binding.editImg.setOnClickListener(v -> tirarFoto());

    }

//    private void configurarInterface() {
//        if (modoEdicao) {
//            binding.editImg.setVisibility(View.VISIBLE);
//            iniciarCameraX();
//        } else {
//            binding.editImg.setVisibility(View.GONE);
//        }
//    }

    private void configurarInterface() {
        if (modoEdicao) {
            binding.btnLogout.setVisibility(View.VISIBLE);
        } else {
            binding.btnLogout.setVisibility(View.GONE);
        }
    }

//    private void iniciarCameraX() {
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                Preview preview = new Preview.Builder().build();
//                preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider());
//
//                imageCapture = new ImageCapture.Builder()
//                        .setTargetRotation(requireActivity().getWindowManager().getDefaultDisplay().getRotation())
//                        .build();
//
//                cameraProvider.unbindAll();
//                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
//            } catch (Exception e) {
//                Log.e("CameraX", "Erro ao iniciar", e);
//            }
//        }, ContextCompat.getMainExecutor(requireContext()));
//    }

//    private void tirarFoto() {
//        if (imageCapture == null) return;
//
//        // Se estiver editando o Tutor, a foto pode ser salva com o idTutor
//        String fileName = (idTutor != null) ? "tutor_" + idTutor : "pet_" + idPet;
//        File file = new File(requireContext().getExternalFilesDir(null), fileName + ".jpg");
//        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(file).build();
//
//        imageCapture.takePicture(options, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults results) {
//                Uri uri = Uri.fromFile(file);
//                binding.imageView10.setImageURI(uri);
//                fazerUploadFirebase(uri);
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException exc) {
//                if(isAdded()) Toast.makeText(getContext(), "Erro ao capturar foto", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void fazerUploadFirebase(Uri uri) {
//        if (idTutor == null) return;
//        if(isAdded()) Toast.makeText(getContext(), "Fazendo upload da foto...", Toast.LENGTH_SHORT).show();
//
//        StorageReference ref = storage.getReference().child("fotos_tutores/" + idTutor + ".jpg");
//
//        ref.putFile(uri).addOnSuccessListener(task -> {
//            ref.getDownloadUrl().addOnSuccessListener(url -> {
//                salvarLinkNoPostgres(url.toString());
//            });
//        }).addOnFailureListener(e -> {
//            if(isAdded()) Toast.makeText(getContext(), "Falha no Firebase", Toast.LENGTH_SHORT).show();
//        });
//    }

//    private void salvarLinkNoPostgres(String url) {
//        TutorRequest request = new TutorRequest();
//        request.setImagem(url);
//
//        apiPostgres.atualizarFotoTutor(idTutor, request).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    if (isAdded()) {
//                        Toast.makeText(getContext(), "Foto do perfil atualizada!", Toast.LENGTH_SHORT).show();
//
//                        Glide.with(Perfil.this)
//                                .load(url)
//                                .circleCrop()
//                                .into(binding.imageView12);
//                    }
//                } else {
//                    Log.e("API_ERROR", "Erro ao atualizar: " + response.code());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                Log.e("API_ERROR", "Falha na conexão ao salvar link da foto", t);
//            }
//        });
//    }


    private void carregarDadosDoPerfil() {
        if (idPet == null) return;

        apiPostgres.getCachorroPorId(idPet).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(@NonNull Call<Cachorro> call, @NonNull Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null ) {
                    preencherDadosCachorro(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Cachorro> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erro ao carregar pet", t);
            }
        });

        apiPostgres.getIdTutorCachorro(idPet).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    idTutor = response.body();

                    if (idTutor != null) {
                        buscarInformacoesDoTutor(idTutor);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
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
                    binding.txtNomeTutor.setText(tutor.getNome());
                    binding.txtIdadeTutor.setText(calcularIdade(tutor.getDataNascimento()) + " anos" );

                    if (isAdded()) {
                        Glide.with(Perfil.this)
                                .load(tutor.getImagem())
                                .circleCrop()
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(binding.imgTutor);
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
        binding.txtNomePet.setText(cachorro.getNome());
        binding.racaidade.setText(calcularIdade(cachorro.getDataNascimento()) + " anos" + " * " +cachorro.getRaca());
        if (cachorro.getSexo()== "M"){
            binding.sexo.setText(cachorro.getSexo()+"acho");
        }
        else {
            binding.sexo.setText(cachorro.getSexo()+"êmea");
        }
        binding.turma.setText("Turma "+cachorro.getTurma());

        Glide.with(requireContext())
                .load(cachorro.getImagem())
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgPet);

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