package com.example.project_prm392.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.UserEntity;
import com.example.project_prm392.ui.home.HomeActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private AppDatabase appDatabase; // Thêm biến database
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        // Ánh xạ các thành phần View
        initViews();
        // Thiết lập Toolbar
        setupToolbar();

        // Xử lý sự kiện click cho nút Đăng nhập
        buttonLogin.setOnClickListener(v -> loginUser());

        // Xử lý sự kiện click cho text Đăng ký
        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarLogin); // <-- Ánh xạ Toolbar
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Đăng Nhập"); // Đặt tiêu đề nếu muốn
        }
    }

    private void loginUser() {
        // Logic đăng nhập giữ nguyên
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            UserEntity user = appDatabase.shopDao().findUserByEmail(email);
            runOnUiThread(() -> {
                if (user != null && user.password.equals(password)) {
                    handleLoginSuccess(user);
                } else {
                    Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void handleLoginSuccess(UserEntity user) {
        // Logic lưu SharedPreferences giữ nguyên
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("userId", user.userId);
        editor.putString("userName", user.fullName);
        editor.putString("userEmail", user.email);
        editor.apply();

        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

        // Chuyển đến màn hình chính
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // THÊM HÀM MỚI ĐỂ XỬ LÝ NÚT QUAY LẠI TRÊN TOOLBAR
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity hiện tại để quay về màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
