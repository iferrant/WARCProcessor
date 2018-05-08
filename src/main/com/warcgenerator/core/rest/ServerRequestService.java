package com.warcgenerator.core.rest;

import com.warcgenerator.core.rest.endpoints.Corpus;
import com.warcgenerator.core.rest.endpoints.Login;
import com.warcgenerator.core.rest.models.CorpusResponse;
import com.warcgenerator.core.rest.models.Token;
import com.warcgenerator.core.rest.models.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;

public class ServerRequestService {

    /**
     * Request a new token based on the user credentials
     *
     * @param user User to login
     * @param responseCallback Callback to retrieve the API response
     */
    public void authenticateUser(User user, final RequestResponse<Token> responseCallback) {
        final Login loginService = ServiceGenerator.createService(Login.class);

        Call<Token> tokenCall = loginService.login(user.getEmail(), user.getPassword());
        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                if (response != null && response.body() != null && response.isSuccessful()) {
                    responseCallback.onRequestSuccess(response.body());
                } else {
                    responseCallback.onRequestFail("Login response not successful");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable throwable) {
                responseCallback.onRequestFail(throwable.getMessage());
            }
        });
    }

    /**
     * Upload new corpus to the API
     */
    public void postCorpus(String corpusName, String corpusPath) {
        File corpusFile = new File(corpusPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), corpusFile);
        // The 'name' field of the 'createFormData()' method,
        // must be the form label name for the corpus file
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", corpusName, requestBody);

        RequestBody name = RequestBody.create(MultipartBody.FORM, corpusName);

        final Corpus corpusService = ServiceGenerator.createService(Corpus.class);

        Call<CorpusResponse> corpusCall = corpusService.corpus(name, body);
        corpusCall.enqueue(new Callback<CorpusResponse>() {
            @Override
            public void onResponse(Call<CorpusResponse> call, Response<CorpusResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    System.out.println("Corpus uploaded!");
                } else {
                    System.err.println("Corpus upload not successful");
                }
            }

            @Override
            public void onFailure(Call<CorpusResponse> call, Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        });
    }
}
