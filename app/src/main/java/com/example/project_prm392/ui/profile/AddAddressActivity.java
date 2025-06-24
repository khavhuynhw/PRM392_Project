package com.example.project_prm392.ui.profile;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
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
import com.example.project_prm392.model.Address;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class AddAddressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputEditText fullNameEditText, phoneEditText, cityEditText, streetEditText;
    private SwitchMaterial setDefaultSwitch;
    private Button saveAddressButton;
    private Address addressToEdit;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Khởi tạo database
        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        initToolbar();
        initViews();

        if (getIntent().hasExtra("EDIT_ADDRESS")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                addressToEdit = getIntent().getParcelableExtra("EDIT_ADDRESS", Address.class);
            } else {
                addressToEdit = getIntent().getParcelableExtra("EDIT_ADDRESS");
            }
            setupEditMode();
        }

        saveAddressButton.setOnClickListener(v -> saveAddress());
    }

    // Thiết lập giao diện cho chế độ Sửa
    private void setupEditMode() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sửa Địa Chỉ");
        }
        saveAddressButton.setText("Cập nhật địa chỉ");
        if (addressToEdit != null) {
            populateFields(addressToEdit);
        }
    }

    // Đổ dữ liệu cũ vào form
    private void populateFields(Address address) {
        fullNameEditText.setText(address.getRecipientName());
        phoneEditText.setText(address.getPhone());
        cityEditText.setText(address.getCity());
        streetEditText.setText(address.getStreetAddress());
        setDefaultSwitch.setChecked(address.isDefault());
    }

    // Ánh xạ Views
    private void initViews() {
        fullNameEditText = findViewById(R.id.addFullNameEditText);
        phoneEditText = findViewById(R.id.addPhoneEditText);
        cityEditText = findViewById(R.id.addCityEditText);
        streetEditText = findViewById(R.id.addStreetEditText);
        setDefaultSwitch = findViewById(R.id.setDefaultSwitch);
        saveAddressButton = findViewById(R.id.saveAddressButton);
    }

    // Thiết lập Toolbar
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarAddAddress);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void saveAddress() {
        // Lấy dữ liệu từ form
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        boolean isDefault = setDefaultSwitch.isChecked();

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(city) || TextUtils.isEmpty(street)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy userId của người dùng đang đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chạy tác vụ trên luồng nền
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Nếu người dùng chọn "Mặc định", trước tiên hãy bỏ mặc định tất cả các địa chỉ khác của họ
            if (isDefault) {
                appDatabase.shopDao().clearDefaultAddress(userId);
            }

            if (addressToEdit != null) {
                // --- Chế độ CẬP NHẬT ---
                // Cập nhật lại các trường của đối tượng cũ
                addressToEdit.setRecipientName(fullName);
                addressToEdit.setPhone(phone);
                addressToEdit.setCity(city);
                addressToEdit.setStreetAddress(street);
                addressToEdit.setDefault(isDefault);

                appDatabase.shopDao().updateAddress(addressToEdit);
                runOnUiThread(() -> Toast.makeText(this, "Đã cập nhật địa chỉ!", Toast.LENGTH_SHORT).show());
            } else {
                // --- Chế độ THÊM MỚI ---
                Address newAddress = new Address(fullName, phone, street, city, isDefault, userId);
                appDatabase.shopDao().insertAddress(newAddress);
                runOnUiThread(() -> Toast.makeText(this, "Đã lưu địa chỉ mới!", Toast.LENGTH_SHORT).show());
            }

            // Đóng màn hình sau khi lưu thành công
            finish();
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