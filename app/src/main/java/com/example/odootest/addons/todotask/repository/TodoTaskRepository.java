package com.example.odootest.addons.todotask.repository;
//ihihi
import com.example.odootest.addons.todotask.model.TodoTask;
import com.example.odootest.model.OdooRpcRequest;
import com.example.odootest.model.OdooRpcResponse;
import com.example.odootest.network.OdooApiService;
import com.example.odootest.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class TodoTaskRepository {
    private final OdooApiService apiService = RetrofitClient.getClient();
    private final Gson gson = new Gson();

    public Single<List<TodoTask>> getTasks(String url, String db, int uid, String password, String searchQuery) {
        String fullUrl = url.endsWith("/") ? url + "jsonrpc" : url + "/jsonrpc";
        String[] fields = {"name", "description", "due_date", "done"};
        HashMap<String, Object> kwargs = new HashMap<>();
        kwargs.put("fields", fields);
        // Tạo domain tìm kiếm
        List<Object> domain;
        if (searchQuery == null || searchQuery.isEmpty()) {
            domain = new ArrayList<>();
        } else {
            domain = Collections.singletonList(Arrays.asList("name", "ilike", searchQuery));
        }
        OdooRpcRequest request = new OdooRpcRequest(
                "object", "execute_kw",
                db, uid, password, "todo.task", "search_read",
                Collections.singletonList(domain), // Truyền domain vào đây
                kwargs
        );
        return apiService.call(fullUrl, request).map(this::parseResponse);
    }

    public Single<Integer> createTask(String url, String db, int uid, String password, String name, String description, String dueDate, boolean isDone) {
        String fullUrl = url.endsWith("/") ? url + "jsonrpc" : url + "/jsonrpc";
        HashMap<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("due_date", dueDate.isEmpty() ? false : dueDate);
        values.put("done", isDone);
        OdooRpcRequest request = new OdooRpcRequest(
                "object", "execute_kw",
                db, uid, password, "todo.task", "create",
                Collections.singletonList(values)
        );
        return apiService.call(fullUrl, request).map(response -> {
            if (!response.isSuccessful()) throw new Exception(response.error.data.message);
            return response.result.getAsInt();
        });
    }

    public Single<Boolean> updateTask(String url, String db, int uid, String password, int taskId, String name, String description, String dueDate, boolean done) {
        String fullUrl = url.endsWith("/") ? url + "jsonrpc" : url + "/jsonrpc";
        HashMap<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("due_date", dueDate.isEmpty() ? false : dueDate);
        values.put("done", done);
        OdooRpcRequest request = new OdooRpcRequest(
                "object", "execute_kw",
                db, uid, password, "todo.task", "write",
                Arrays.asList(Collections.singletonList(taskId), values)
        );
        return apiService.call(fullUrl, request).map(response -> {
            if (!response.isSuccessful()) throw new Exception(response.error.data.message);
            return response.result.getAsBoolean();
        });
    }

    public Single<Boolean> deleteTask(String url, String db, int uid, String password, int taskId) {
        String fullUrl = url.endsWith("/") ? url + "jsonrpc" : url + "/jsonrpc";

        OdooRpcRequest request = new OdooRpcRequest(
                "object", "execute_kw",
                db, uid, password, "todo.task", "unlink",
                Collections.singletonList(Collections.singletonList(taskId))
        );
        return apiService.call(fullUrl, request).map(response -> {
            if (!response.isSuccessful()) throw new Exception(response.error.data.message);
            return response.result.getAsBoolean(); // Trả về true nếu thành công
        });
    }

    private List<TodoTask> parseResponse(OdooRpcResponse response) throws Exception {
        if (!response.isSuccessful()) {
            // Nếu có lỗi từ Odoo, ném ra một Exception
            throw new Exception(response.error.data.message);
        }
        Type listType = new TypeToken<ArrayList<TodoTask>>() {}.getType();
        // Dùng Gson để parse JsonElement 'result' thành List<TodoTask>
        return gson.fromJson(response.result, listType);
    }
}