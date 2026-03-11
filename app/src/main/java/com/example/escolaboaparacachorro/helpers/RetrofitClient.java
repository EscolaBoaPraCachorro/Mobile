public class RetrofitClient {
    private static final String BASE_URL = "https://api-lxnr.onrender.com";
    private static ApiPostgres instance;

    public static ApiPostgres getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiPostgres.class);
        }
        return instance;
    }
}