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
        apiPostgres = RetrofitClient.getInstance(); // Uso limpo da API
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

        // Recuperar dados (Intent/Arguments)
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
            binding.descricaoPet.setEnabled(true);
            iniciarCameraX(); // Inicializa o visor da câmera
        }
    }

    // --- LÓGICA DO CAMERAX ---
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
                binding.imageView10.setImageURI(uri); // UI rápida
                fazerUploadFirebase(uri); // Upload otimizado
            }

            @Override
            public void onError(@NonNull ImageCaptureException exc) {
                Toast.makeText(getContext(), "Erro ao capturar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- FIREBASE + POSTGRES ---
    private void fazerUploadFirebase(Uri uri) {
        Toast.makeText(getContext(), "Atualizando foto...", Toast.LENGTH_SHORT).show();
        StorageReference ref = storage.getReference().child("fotos_pets/" + idPet + ".jpg");

        ref.putFile(uri).addOnSuccessListener(task -> {
            ref.getDownloadUrl().addOnSuccessListener(url -> {
                salvarLinkNoPostgres(url.toString());
            });
        });
    }

    private void salvarLinkNoPostgres(String url) {
        apiPostgres.atualizarFoto(idPet, url).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) Toast.makeText(getContext(), "Foto sincronizada!", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
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
                Log.e("API_ERROR", "Erro ao carregar cachorro", t);
            }
        });

        apiPostgres.getIdTutorCachorro(idPet).enqueue(new Callback<Cachorro>() {
            @Override
            public void onResponse(Call<Cachorro> call, Response<Cachorro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cachorro tutor = response.body();
                    binding.textView17.setText(tutor.getNome());
                    binding.idadeT.setText(calcularIdade(tutor.getDataNascimento()) + " anos");
                    Glide.with(requireContext()).load(tutor.getImagem()).circleCrop().into(binding.imageView12);
                }
            }
            @Override
            public void onFailure(Call<Cachorro> call, Throwable t) {}
        });
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
            return String.valueOf(idade);
        } catch (ParseException e) { return "0"; }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
        binding = null;
    }
}