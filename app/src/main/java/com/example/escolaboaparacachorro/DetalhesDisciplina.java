public class DetalhesDisciplina extends Retrofit {

    private FragmentDetalhesDisciplinaBinding binding;
    private ApiPostgres apiPostgres;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalhesDisciplinaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiPostgres = RetrofitClient.getInstance();

        String idAluno = sessionManager.getDogId();

        String nomeDisciplina = "";
        if (getArguments() != null) {
            nomeDisciplina = getArguments().getString("nome_disciplina");
            binding.nomeDisciplina.setText(nomeDisciplina);
        }


        if (idAluno.isEmpty()) {
            Toast.makeText(getContext(), "Erro: Aluno não identificado", Toast.LENGTH_SHORT).show();
            return;
        }


        binding.rvNotas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.voltar.setOnClickListener(v -> getParentFragmentManager().popBackStack());


        carregarDadosIniciais(nomeDisciplina, idAluno);
    }

    private void carregarDadosIniciais(String disciplina, String idAluno) {
        apiPostgres.getIdProfPorDisciplina(disciplina).enqueue(new Callback<Disciplinas>() {
            @Override
            public void onResponse(Call<Disciplinas> call, Response<Disciplinas> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String idProfessor = String.valueOf(response.body().getIdProfessor());

                    buscarFotoProfessor(idProfessor);
                    buscarNotas(idProfessor, idAluno);
                }
            }
            @Override
            public void onFailure(Call<Disciplinas> call, Throwable t) { showError(); }
        });

        buscarObservacoes(disciplina, idAluno);
    }

    private void buscarFotoProfessor(String idProfessor) {
        apiPostgres.getImagemProfPorId(idProfessor).enqueue(new Callback<Professor>() {
            @Override
            public void onResponse(Call<Professor> call, Response<Professor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Glide.with(requireContext()).load(response.body().getImagem()).into(binding.fotoProfessor);
                }
            }
            @Override
            public void onFailure(Call<Professor> call, Throwable t) {}
        });
    }

    private void buscarNotas(String idProfessor, String idAluno) {
        apiPostgres.getNotasPorAlunoDisciplina(idProfessor, idAluno).enqueue(new Callback<List<Notas>>() {
            @Override
            public void onResponse(Call<List<Notas>> call, Response<List<Notas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.rvNotas.setAdapter(new DetalhesNotasAdapter(response.body()));
                }
            }
            @Override
            public void onFailure(Call<List<Notas>> call, Throwable t) { showError(); }
        });
    }

    private void buscarObservacoes(String disciplina, String idAluno) {
        apiPostgres.getObservacaoPorAlunoDisciplina(disciplina, idAluno).enqueue(new Callback<Observacoes>() {
            @Override
            public void onResponse(Call<Observacoes> call, Response<Observacoes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    binding.observacoes.setText(response.body().getDescricao());
                }
            }
            @Override
            public void onFailure(Call<Observacoes> call, Throwable t) { showError(); }
        });
    }

    private void showError() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar dados da escola.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}