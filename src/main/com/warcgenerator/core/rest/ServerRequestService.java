package com.warcgenerator.core.rest;

import com.warcgenerator.core.rest.endpoints.Corpus;
import com.warcgenerator.core.rest.endpoints.Login;
import com.warcgenerator.core.rest.endpoints.Tokens;
import com.warcgenerator.core.rest.models.CorpusResponse;
import com.warcgenerator.core.rest.models.Token;
import com.warcgenerator.core.rest.models.User;
import com.warcgenerator.core.rest.response.CountingRequestBody;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

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
     * Refresh the token to retrieve a new valid token
     *
     * @param responseCallback Callback to retrieve the API response
     */
    public void refreshToken(final RequestResponse<Token> responseCallback) {
        final Tokens tokenService = ServiceGenerator.createService(Tokens.class);

        Call<Token> refreshTokenCall = tokenService.refreshToken();
        refreshTokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                if (response != null && response.body() != null && response.isSuccessful()) {
                    responseCallback.onRequestSuccess(response.body());
                } else {
                    responseCallback.onRequestFail("Refresh token response not successful");
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
    public Flowable<Double> postCorpus(String corpusPath) {

        RequestBody name = RequestBody.create(MultipartBody.FORM, corpusPath);

        final Corpus corpusService = ServiceGenerator.createService(Corpus.class);

        return Flowable.create(emitter -> {
            try {
                CorpusResponse response =
                        corpusService.corpus(name, createMultipartBody(corpusPath, emitter)).blockingGet();
                // Reference to thread to interrupt the process when the user press the cancel button
                Thread currentThread = Thread.currentThread();
                emitter.setCancellable(currentThread::interrupt);
                emitter.onComplete();
            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    /**
     * Creates {@link MultipartBody} with the corpus path and the file to upload
     * The body of the request also includes an {@link FlowableEmitter} to track
     * the upload progress
     *
     * @param corpusPath Corpus path
     * @param emitter {@link FlowableEmitter} double emitter
     * @return {@link MultipartBody.Part} object
     */
    private MultipartBody.Part createMultipartBody(String corpusPath, FlowableEmitter<Double> emitter) {
        // The 'name' field of the 'createFormData()' method,
        // must be the form label name for the corpus file
        return MultipartBody
                .Part
                .createFormData("file", corpusPath, createCountingRequestBody(corpusPath, emitter));
    }

    /**
     * Creates the request's body
     * @param corpusPath Path of the file to upload
     * @param emitter {@link FlowableEmitter} double emitter
     * @return {@link RequestBody} object
     */
    private RequestBody createCountingRequestBody(String corpusPath, FlowableEmitter<Double> emitter) {
        File corpusFile = new File(corpusPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), corpusFile);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            double progress = (1.0 * bytesWritten) / contentLength;
            emitter.onNext(progress);
        });
    }
}
