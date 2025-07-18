package com.example.odootest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.odootest.viewmodel.MainViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText etUrl, etDb, etUsername, etPassword;
    private Button btnLogin;
    private TextView tvResult;
    private ProgressBar progressBar;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // Ánh xạ View
        etUrl = findViewById(R.id.etUrl);
        etDb = findViewById(R.id.etDb);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvResult = findViewById(R.id.tvResult);
        progressBar = findViewById(R.id.progressBar);

        // Lắng nghe sự kiện click
        btnLogin.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            String db = etDb.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (url.isEmpty() || db.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.loginToOdoo(url, db, username, password);
        });

        // Lắng nghe thay đổi từ ViewModel
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
            }
        });

        viewModel.getLoginResult().observe(this, result -> {
            tvResult.setText(result);
            // KIỂM TRA NẾU ĐĂNG NHẬP THÀNH CÔNG
            if (result.startsWith("Success!")) {
                try {
                    // Lấy uid từ chuỗi kết quả
                    String uidString = result.substring(result.indexOf("=") + 1).trim();
                    int uid = Integer.parseInt(uidString);

                    // Lấy thông tin cần thiết
                    String url = etUrl.getText().toString().trim();
                    String db = etDb.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    // Tạo Intent để mở TodoTaskActivity
                    Intent intent = new Intent(MainActivity.this, com.example.odootest.addons.todotask.view.TodoTaskActivity.class);
                    intent.putExtra("ODOO_URL", url);
                    intent.putExtra("ODOO_DB", db);
                    intent.putExtra("ODOO_UID", uid);
                    intent.putExtra("ODOO_PASSWORD", password);

                    startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(this, "Failed to parse UID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}