package com.example.project_prm392.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_prm392.R;
import com.example.project_prm392.model.CartItemWithProduct;
import java.util.List;

public class CheckoutProductAdapter extends RecyclerView.Adapter<CheckoutProductAdapter.ViewHolder> {
    private List<CartItemWithProduct> items;

    public CheckoutProductAdapter(List<CartItemWithProduct> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemWithProduct item = items.get(position);
        holder.name.setText(item.product.getName() + " (x" + item.cartItem.quantity + ")");
        double totalPrice = item.product.getPrice() * item.cartItem.quantity;
        holder.price.setText(String.format("%,.0fđ", totalPrice));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<CartItemWithProduct> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.checkoutProductNameTextView);
            price = itemView.findViewById(R.id.checkoutProductPriceTextView);
        }
    }
}
