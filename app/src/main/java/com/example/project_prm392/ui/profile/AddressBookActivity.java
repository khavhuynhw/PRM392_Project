package com.example.project_prm392.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.Address;
import com.example.project_prm392.ui.adapter.AddressAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddressBookActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private FloatingActionButton fabAddAddress;
    private AppDatabase appDatabase;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("userId", -1);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        initToolbar();
        initViews();
        setupRecyclerView();

        fabAddAddress.setOnClickListener(v -> {
            startActivity(new Intent(AddressBookActivity.this, AddAddressActivity.class));
        });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarAddressBook);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.addressRecyclerView);
        fabAddAddress = findViewById(R.id.fabAddAddress);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo adapter với danh sách rỗng
        adapter = new AddressAdapter(new ArrayList<>(), currentUserId);
        recyclerView.setAdapter(adapter);

        appDatabase.shopDao().getAddressesForUser(currentUserId).observe(this, addresses -> {
            if (addresses != null) {
                adapter.setAddresses(addresses);
            }
        });

        // Bắt đầu tải dữ liệu từ database
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        // Lấy userId của người dùng đang đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1); // -1 là giá trị mặc định nếu không tìm thấy

        if (currentUserId == -1) {
            // Xử lý trường hợp người dùng chưa đăng nhập nhưng vẫn vào được màn hình này (nếu có thể)
            // Hoặc đơn giản là không làm gì cả, danh sách sẽ trống
            return;
        }

        // Bắt đầu quan sát (observe) dữ liệu địa chỉ của người dùng
        appDatabase.shopDao().getAddressesForUser(currentUserId).observe(this, addresses -> {
            // Khi có bất kỳ thay đổi nào trong bảng addresses của user này,
            // đoạn code này sẽ được thực thi với danh sách `addresses` mới nhất.
            if (addresses != null) {
                adapter.setAddresses(addresses); // Cập nhật dữ liệu cho adapter
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