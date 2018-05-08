package com.warcgenerator.core.rest.endpoints;

import com.warcgenerator.core.rest.models.CorpusResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Corpus {
    @Multipart
    @POST("corpus")
    Call<CorpusResponse> corpus(@Part("name") RequestBody name, @Part MultipartBody.Part file);
}
