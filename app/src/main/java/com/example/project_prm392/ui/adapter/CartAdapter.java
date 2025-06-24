package com.example.project_prm392.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.DAO.AppDatabase;
import com.example.project_prm392.R;
import com.example.project_prm392.model.CartItem;
import com.example.project_prm392.model.CartItemWithProduct;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemWithProduct> cartItems;
    private AppDatabase appDatabase;

    public CartAdapter(List<CartItemWithProduct> cartItems, Context context) {
        this.cartItems = cartItems;
        this.appDatabase = AppDatabase.getDatabase(context.getApplicationContext());
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemWithProduct item = cartItems.get(position);

        // Lấy dữ liệu từ product và cartItem để hiển thị
        holder.productNameTextView.setText(item.product.getName());
        holder.productPriceTextView.setText(String.format("%,.0fđ", item.product.getPrice()));
        holder.productImageView.setImageResource(item.product.getImageResourceId());
        holder.quantityTextView.setText(String.valueOf(item.cartItem.quantity));

        // Xử lý sự kiện nút Xóa
        holder.removeItemButton.setOnClickListener(v -> {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                appDatabase.shopDao().deleteCartItem(item.cartItem);
            });
        });

        // Xử lý sự kiện nút Tăng số lượng
        holder.increaseQuantityButton.setOnClickListener(v -> {
            item.cartItem.quantity++;
            AppDatabase.databaseWriteExecutor.execute(() -> {
                appDatabase.shopDao().updateCartItem(item.cartItem);
            });
        });

        // Xử lý sự kiện nút Giảm số lượng
        holder.decreaseQuantityButton.setOnClickListener(v -> {
            if (item.cartItem.quantity > 1) {
                item.cartItem.quantity--;
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    appDatabase.shopDao().updateCartItem(item.cartItem);
                });
            } else {
                // Nếu số lượng là 1, giảm nữa thì xóa
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    appDatabase.shopDao().deleteCartItem(item.cartItem);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // Hàm để cập nhật danh sách từ LiveData
    public void setCartItems(List<CartItemWithProduct> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView quantityTextView;
        ImageButton removeItemButton, increaseQuantityButton, decreaseQuantityButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            removeItemButton = itemView.findViewById(R.id.removeItemButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
        }
    }
}
