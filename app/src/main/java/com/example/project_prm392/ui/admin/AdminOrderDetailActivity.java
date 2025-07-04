package com.example.project_prm392.ui.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_prm392.R;
import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.model.Order;
import com.example.project_prm392.model.OrderWithProducts;
import com.example.project_prm392.ui.adapter.OrderDetailProductAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminOrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderId, tvCustomerName, tvOrderDate, tvOrderStatus, tvOrderAddress, tvOrderTotal;
    private RecyclerView recyclerViewOrderProducts;
    private Button btnUpdateStatus;
    private OrderWithProducts orderWithProducts;
    private OrderDetailProductAdapter productAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderAddress = findViewById(R.id.tvOrderAddress);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        recyclerViewOrderProducts = findViewById(R.id.recyclerViewOrderProducts);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        recyclerViewOrderProducts.setLayoutManager(new LinearLayoutManager(this));

        long orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getDatabase(this);
        db.shopDao().getAllOrdersWithProducts().observe(this, new Observer<java.util.List<com.example.project_prm392.model.OrderWithProducts>>() {
            @Override
            public void onChanged(java.util.List<com.example.project_prm392.model.OrderWithProducts> orders) {
                for (OrderWithProducts owp : orders) {
                    if (owp.order.orderId == orderId) {
                        orderWithProducts = owp;
                        showOrderDetail();
                        break;
                    }
                }
            }
        });

        btnUpdateStatus.setOnClickListener(v -> showStatusDialog(db, orderId));
    }

    private void showOrderDetail() {
        if (orderWithProducts == null) return;
        Order order = orderWithProducts.order;
        tvOrderId.setText("Mã đơn: #" + order.orderId);
        tvCustomerName.setText("Khách hàng: " + order.recipientName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvOrderDate.setText("Ngày đặt: " + dateFormat.format(new Date(order.getOrderDate())));
        tvOrderStatus.setText("Trạng thái: " + order.getStatus());
        tvOrderAddress.setText("Địa chỉ: " + order.shippingAddress);
        tvOrderTotal.setText("Tổng tiền: " + String.format("%,.0fđ", order.getTotalAmount()));
        productAdapter = new com.example.project_prm392.ui.adapter.OrderDetailProductAdapter(
                new ArrayList<>(orderWithProducts.products),
                new ArrayList<>(orderWithProducts.crossRefs)
        );
        recyclerViewOrderProducts.setAdapter(productAdapter);
    }

    private void showStatusDialog(AppDatabase db, long orderId) {
        final String[] statuses = {"Đang xử lý", "Đã giao", "Đã hủy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật trạng thái đơn hàng")
                .setItems(statuses, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newStatus = statuses[which];
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            db.shopDao().updateOrderStatus(orderId, newStatus);
                        });
                        runOnUiThread(() -> {
                            tvOrderStatus.setText("Trạng thái: " + newStatus);
                            Toast.makeText(AdminOrderDetailActivity.this, "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
} 