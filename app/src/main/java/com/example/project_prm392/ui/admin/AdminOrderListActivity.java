package com.example.project_prm392.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_prm392.R;
import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.model.OrderWithProducts;
import com.example.project_prm392.ui.adapter.OrderAdapter;
import java.util.ArrayList;
import java.util.List;

public class AdminOrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private ArrayList<OrderWithProducts> orderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new OrderAdapter(orderList);
        recyclerViewOrders.setAdapter(orderAdapter);

        AppDatabase db = AppDatabase.getDatabase(this);
        db.shopDao().getAllOrdersWithProducts().observe(this, new Observer<List<OrderWithProducts>>() {
            @Override
            public void onChanged(List<OrderWithProducts> orders) {
                if (orders != null) {
                    orderAdapter.setOrders(orders);
                }
            }
        });

        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrderWithProducts order) {
                Intent intent = new Intent(AdminOrderListActivity.this, AdminOrderDetailActivity.class);
                intent.putExtra("orderId", order.order.orderId);
                startActivity(intent);
            }
        });
    }
} 