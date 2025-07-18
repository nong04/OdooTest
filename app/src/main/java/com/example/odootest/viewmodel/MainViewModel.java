package com.example.odootest.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.odootest.model.OdooRpcRequest;
import com.example.odootest.model.OdooRpcResponse;
import com.example.odootest.network.OdooApiService;
import com.example.odootest.network.RetrofitClient;
import com.google.gson.JsonElement;

import java.util.Collections;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final OdooApiService apiService = RetrofitClient.getClient();

    public LiveData<String> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loginToOdoo(String url, String db, String username, String password) {
        isLoading.setValue(true);
        // Endpoint đầy đủ là URL/jsonrpc
        String fullUrl = url.endsWith("/") ? url + "jsonrpc" : url + "/jsonrpc";
        // Tạo request body
        OdooRpcRequest request = new OdooRpcRequest("common", "login", db, username, password);
        disposables.add(
                apiService.call(fullUrl, request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleSuccess, this::handleError)
        );
    }

    private void handleSuccess(OdooRpcResponse response) {
        isLoading.setValue(false);
        if (response.isSuccessful()) {
            JsonElement result = response.result;
            // Odoo trả về false nếu login thất bại, hoặc uid (int) nếu thành công
            if (result.isJsonPrimitive() && result.getAsJsonPrimitive().isNumber()) {
                int uid = result.getAsInt();
                loginResult.setValue("Success! UID = " + uid);
            } else {
                loginResult.setValue("Login Failed: Invalid credentials or database.");
            }
        } else {
            loginResult.setValue("Error: " + response.error.data.message);
        }
    }

    private void handleError(Throwable throwable) {
        isLoading.setValue(false);
        loginResult.setValue("Network Error: " + throwable.getMessage());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear(); // Hủy các subscription để tránh memory leak
    }
}