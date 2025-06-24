package com.example.project_prm392.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.CartItem;
import com.example.project_prm392.model.Product;
import com.example.project_prm392.ui.auth.LoginActivity;
import com.example.project_prm392.ui.product.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<Product> products;
    private AppDatabase appDatabase;

    public ProductAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        appDatabase = AppDatabase.getDatabase(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productImage.setImageResource(product.getImageResourceId());
        holder.productName.setText(product.getName());
        String priceText = String.format("%,.0fđ %s", product.getPrice(), product.getUnit());
        holder.productPrice.setText(priceText);

        holder.addToCartButton.setOnClickListener(v -> {
            addItemToCart(v.getContext(), product);
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                // Tạo Intent để mở ProductDetailActivity
                Intent intent = new Intent(context, ProductDetailActivity.class);

                // Đính kèm đối tượng Product đã được chọn vào Intent
                intent.putExtra("PRODUCT_DETAIL", product);

                // Khởi chạy Activity
                context.startActivity(intent);
            }
        });
    }

    private void addItemToCart(Context context, Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            // Nếu chưa đăng nhập, hiển thị hộp thoại yêu cầu
            new AlertDialog.Builder(context)
                    .setTitle("Yêu cầu đăng nhập")
                    .setMessage("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.")
                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return;
        }

        final int quantityToAdd = 1;

        // Chạy tác vụ trên luồng nền
        AppDatabase.databaseWriteExecutor.execute(() -> {
            CartItem existingItem = appDatabase.shopDao().getCartItemByUserAndProduct(userId, product.productId);

            if (existingItem != null) {
                // Sản phẩm đã có, tăng số lượng
                existingItem.quantity += quantityToAdd;
                appDatabase.shopDao().updateCartItem(existingItem);
            } else {
                // Sản phẩm chưa có, tạo mới
                CartItem newItem = new CartItem(product.productId, userId, quantityToAdd);
                appDatabase.shopDao().insertCartItem(newItem);
            }

            // Hiển thị thông báo trên luồng UI
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Đã thêm " + product.getName(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        ImageButton addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImageView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }

    public void setProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }
}