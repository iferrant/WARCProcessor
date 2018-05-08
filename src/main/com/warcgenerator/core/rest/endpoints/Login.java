package com.warcgenerator.core.rest.endpoints;

import com.warcgenerator.core.rest.models.Token;
import retrofit2.Call;
import retrofit2.http.*;

public interface Login {
    @FormUrlEncoded
    @POST("login")
    Call<Token> login(@Field("email") String email,
                      @Field("password") String password);
}
