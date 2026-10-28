package com.sante.app.api;

import com.sante.app.BuildConfig;
import com.sante.app.data.TokenStore;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit. L'URL du backend vient de BuildConfig.API_BASE_URL
 * (définie dans local.properties -> API_BASE_URL), jamais en dur.
 */
public final class ApiClient {

    private static volatile ApiService service;

    private ApiClient() {}

    public static ApiService getService() {
        if (service == null) {
            synchronized (ApiClient.class) {
                if (service == null) {
                    OkHttpClient.Builder http = new OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(8, TimeUnit.SECONDS);

                    // Ajoute "Authorization: Bearer <jwt>" ; un 401 signifie token expiré -> déconnexion
                    http.addInterceptor(chain -> {
                        Request request = chain.request();
                        String token = TokenStore.getToken();
                        if (token != null && request.header("Authorization") == null) {
                            request = request.newBuilder()
                                    .header("Authorization", "Bearer " + token).build();
                        }
                        Response response = chain.proceed(request);
                        if (response.code() == 401 && token != null) {
                            TokenStore.clear();
                        }
                        return response;
                    });

                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                        http.addInterceptor(logging);
                    }

                    service = new Retrofit.Builder()
                            .baseUrl(BuildConfig.API_BASE_URL)
                            .client(http.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiService.class);
                }
            }
        }
        return service;
    }
}
