package com.warcgenerator.core.rest;

import com.warcgenerator.AppWarc;
import com.warcgenerator.core.rest.endpoints.Login;
import com.warcgenerator.core.rest.models.Token;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServiceGenerator {
    public static final String API_URL = "http://x.atopa.me:8080/api/";
    public static final String DEBUG_API_URL = "http://localhost:8080/api/";

    public static <S> S createService(Class<S> serviceClass) {
        // Create a OkHttpClient shared instance
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        // Don't add token interceptor to login request
        if (!serviceClass.getName().equals(Login.class.getName())) {
            // Add the interceptor to insert the token on each request's header
            builder.interceptors().add(new AuthInterceptor(AppWarc.userGlobal.getToken()));
        }

        // Build OkHttpClient object
        OkHttpClient client = builder.build();

        // Set and return the custom client when building adapter
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(serviceClass);

    }

    private static class AuthInterceptor implements Interceptor {
        private Token token;

        public AuthInterceptor(Token token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain
                    .request()
                    .newBuilder()
                    .header("Authorization", "Bearer " + token.getToken())
                    .build();
            return chain.proceed(request);
        }
    }
}
