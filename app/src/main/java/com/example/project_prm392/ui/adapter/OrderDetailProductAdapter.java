package com.example.project_prm392.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.R;
import com.example.project_prm392.model.OrderProductCrossRef;
import com.example.project_prm392.model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailProductAdapter extends RecyclerView.Adapter<OrderDetailProductAdapter.ViewHolder> {
    // Thay đổi: Nhận cả danh sách sản phẩm và danh sách số lượng
    private List<Product> products;
    private List<OrderProductCrossRef> crossRefs;

    public OrderDetailProductAdapter(ArrayList<Product> products, ArrayList<OrderProductCrossRef> crossRefs) {
        this.products = products;
        this.crossRefs = crossRefs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        int quantity = 1; // Giá trị mặc định

        // Tìm số lượng tương ứng với sản phẩm
        if (crossRefs != null) {
            for (OrderProductCrossRef ref : crossRefs) {
                if (ref.productId == product.productId) {
                    quantity = ref.quantity;
                    break;
                }
            }
        }

        // Hiển thị tên kèm số lượng
        holder.name.setText(product.getName() + " (x" + quantity + ")");
        // Hiển thị giá đã nhân với số lượng
        holder.price.setText(String.format("%,.0fđ", product.getPrice() * quantity));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.detailProductNameTextView);
            price = itemView.findViewById(R.id.detailProductPriceTextView);
        }
    }
}