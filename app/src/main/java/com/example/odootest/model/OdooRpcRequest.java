package com.example.odootest.model;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class OdooRpcRequest {
    @SerializedName("jsonrpc")
    private String jsonrpc = "2.0";

    @SerializedName("method")
    private String method = "call";

    @SerializedName("params")
    private Map<String, Object> params = new HashMap<>();

    public OdooRpcRequest(String service, String method, Object... args) {
        this.params.put("service", service);
        this.params.put("method", method);
        this.params.put("args", args);
    }
}