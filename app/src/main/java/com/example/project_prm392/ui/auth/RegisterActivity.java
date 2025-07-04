package com.example.project_prm392.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewLoginLink;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        // Gán sự kiện cho nút Đăng ký
        buttonRegister.setOnClickListener(v -> registerUser());

        // Gán sự kiện cho link quay về Đăng nhập
        textViewLoginLink.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        // Lấy dữ liệu từ các ô nhập liệu
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // --- Bước 1: Kiểm tra dữ liệu đầu vào ---
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Bước 2: Tạo đối tượng UserEntity ---
        UserEntity newUser = new UserEntity();
        newUser.fullName = fullName;
        newUser.email = email;
        newUser.password = password; // Trong ứng dụng thật, bạn nên mã hóa mật khẩu này

        // --- Bước 3: Thực hiện thao tác với DB trên luồng nền ---
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Kiểm tra xem email đã tồn tại trong database chưa
            UserEntity existingUser = appDatabase.shopDao().findUserByEmail(email);

            if (existingUser != null) {
                // Nếu email đã tồn tại, thông báo cho người dùng trên luồng UI
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Email đã được đăng ký", Toast.LENGTH_SHORT).show());
            } else {
                // Nếu email chưa tồn tại, tiến hành thêm người dùng mới
                appDatabase.shopDao().insertUser(newUser);

                // Thông báo thành công và đóng màn hình đăng ký
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}