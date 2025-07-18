package com.example.odootest.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class OdooRpcResponse {
    @SerializedName("jsonrpc")
    public String jsonrpc;

    @SerializedName("id")
    public int id;

    // Sử dụng JsonElement để linh hoạt xử lý các kiểu trả về khác nhau
    @SerializedName("result")
    public JsonElement result;

    @SerializedName("error")
    public OdooError error;

    public boolean isSuccessful() {
        return error == null && result != null && !result.isJsonNull();
    }

    public static class OdooError {
        @SerializedName("message")
        public String message;
        @SerializedName("code")
        public int code;
        @SerializedName("data")
        public OdooErrorData data;
    }

    public static class OdooErrorData {
        @SerializedName("name")
        public String name;
        @SerializedName("message")
        public String message;
    }
}