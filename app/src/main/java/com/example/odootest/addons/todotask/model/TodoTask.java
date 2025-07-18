package com.example.odootest.addons.todotask.model;

import com.google.gson.annotations.SerializedName;

public class TodoTask {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    // Sử dụng Object để xử lý trường hợp giá trị là 'false' từ Odoo
    @SerializedName("description")
    private Object description;

    @SerializedName("due_date")
    private Object dueDate;

    @SerializedName("done")
    private boolean done;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        // Odoo có thể trả về `false` nếu trường rỗng
        return description instanceof String ? (String) description : "";
    }

    public String getDueDate() {
        return dueDate instanceof String ? (String) dueDate : "No date";
    }

    public boolean isDone() {
        return done;
    }
}