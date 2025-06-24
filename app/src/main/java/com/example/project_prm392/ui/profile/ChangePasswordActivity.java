package com.example.project_prm392.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.UserEntity;
import com.google.android.material.textfield.TextInputEditText;


public class ChangePasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button savePasswordButton;
    private AppDatabase appDatabase;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("userId", -1);

        initToolbar();
        initViews();

        savePasswordButton.setOnClickListener(v -> validateAndShowConfirmation());
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarChangePassword);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        savePasswordButton = findViewById(R.id.savePasswordButton);
    }

    private void validateAndShowConfirmation() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sau khi kiểm tra hợp lệ, hiển thị hộp thoại xác nhận
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đổi mật khẩu")
                .setMessage("Bạn có chắc chắn muốn thay đổi mật khẩu không?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    // Nếu người dùng đồng ý, mới tiến hành đổi mật khẩu
                    changePassword();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void changePassword() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = appDatabase.shopDao().getUserByIdNow(currentUserId);
            if (user != null && user.password.equals(oldPassword)) {
                // Mật khẩu cũ chính xác, tiến hành cập nhật
                user.password = newPassword;
                appDatabase.shopDao().updateUser(user);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                // Mật khẩu cũ không đúng
                runOnUiThread(() -> Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
