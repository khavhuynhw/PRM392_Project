package com.example.project_prm392.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.CartItem;
import com.example.project_prm392.model.CartItemWithProduct;
import com.example.project_prm392.ui.adapter.CartAdapter;
import com.example.project_prm392.ui.cart.CheckoutActivity;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private TextView subtotalTextView, totalTextView, emptyCartTextView;
    private Button checkoutButton;
    private CartAdapter cartAdapter;
    private AppDatabase appDatabase;
    private List<CartItemWithProduct> currentCartItems; // Lưu danh sách hiện tại để tính tổng

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getDatabase(requireContext());

        initViews(view);
        setupRecyclerView();
        loadCartData();

        checkoutButton.setOnClickListener(v -> {
            if (currentCartItems == null || currentCartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(requireActivity(), CheckoutActivity.class));
            }
        });
    }

    private void initViews(View view) {
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        subtotalTextView = view.findViewById(R.id.subtotalTextView);
        totalTextView = view.findViewById(R.id.totalTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        // Bạn có thể thêm một TextView trong layout để thông báo giỏ hàng trống
        // emptyCartTextView = view.findViewById(R.id.emptyCartTextView);
    }

    private void setupRecyclerView() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartAdapter = new CartAdapter(new ArrayList<>(), requireContext());
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void loadCartData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            // Xử lý trường hợp chưa đăng nhập
            // emptyCartTextView.setVisibility(View.VISIBLE);
            // emptyCartTextView.setText("Vui lòng đăng nhập để xem giỏ hàng");
            return;
        }

        appDatabase.shopDao().getCartWithProducts(userId).observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                // Cập nhật danh sách và giao diện
                this.currentCartItems = items;
                cartAdapter.setCartItems(items);
                calculateTotal();

                // Hiển thị thông báo nếu giỏ hàng trống
                // if (items.isEmpty()) {
                //     emptyCartTextView.setVisibility(View.VISIBLE);
                // } else {
                //     emptyCartTextView.setVisibility(View.GONE);
                // }
            }
        });
    }

    private void calculateTotal() {
        double total = 0;
        if (currentCartItems != null) {
            for (CartItemWithProduct item : currentCartItems) {
                // Tính tổng tiền dựa trên giá sản phẩm và số lượng trong giỏ
                total += item.product.getPrice() * item.cartItem.quantity;
            }
        }
        String totalFormatted = String.format("%,.0fđ", total);
        subtotalTextView.setText(totalFormatted);
        totalTextView.setText(totalFormatted);
    }
}