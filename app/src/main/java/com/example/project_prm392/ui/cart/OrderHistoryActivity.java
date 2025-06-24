package com.example.project_prm392.ui.cart;

import android.content.Context;
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
import com.example.project_prm392.model.Order;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.ui.adapter.OrderAdapter;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        appDatabase = AppDatabase.getDatabase(getApplicationContext());

        // Thiết lập Toolbar
        toolbar = findViewById(R.id.toolbarOrderHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.orderHistoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Load dữ liệu từ database
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        // Lấy userId của người dùng đang đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        if (currentUserId == -1) {
            // Chưa đăng nhập, không làm gì cả
            return;
        }

        // Bắt đầu quan sát (observe) danh sách đơn hàng của người dùng
        appDatabase.shopDao().getOrdersForUser(currentUserId).observe(this, orderWithProductsList -> {
            // Khi dữ liệu thay đổi, cập nhật lại adapter
            if (orderWithProductsList != null) {
                adapter.setOrders(orderWithProductsList);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Đóng Activity khi nhấn nút back trên toolbar
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}