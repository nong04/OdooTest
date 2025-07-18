package com.example.odootest.network;

import com.example.odootest.model.OdooRpcRequest;
import com.example.odootest.model.OdooRpcResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface OdooApiService {
    // Sử dụng @Url để có thể thay đổi URL một cách linh hoạt
    @POST
    Single<OdooRpcResponse> call(@Url String url, @Body OdooRpcRequest body);
}