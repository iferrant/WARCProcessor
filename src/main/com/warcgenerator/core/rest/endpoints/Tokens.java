package com.warcgenerator.core.rest.endpoints;

import com.warcgenerator.core.rest.models.Token;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Tokens {
    @GET("token")
    Call<Token> refreshToken();
}
