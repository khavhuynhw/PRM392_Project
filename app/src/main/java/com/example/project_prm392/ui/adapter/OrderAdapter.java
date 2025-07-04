package com.example.project_prm392.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_prm392.R;
import com.example.project_prm392.model.Order;
import com.example.project_prm392.model.OrderProductCrossRef;
import com.example.project_prm392.model.OrderWithProducts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderWithProducts> orderList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(OrderWithProducts order);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public OrderAdapter(ArrayList<OrderWithProducts> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderWithProducts orderWithProducts = orderList.get(position);
        Order order = orderWithProducts.order;

        Context context = holder.itemView.getContext();

        holder.orderId.setText("Mã đơn: #" + order.orderId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.orderDate.setText("Ngày đặt: " + dateFormat.format(new Date(order.getOrderDate())));

        double subtotal = order.getTotalAmount() - order.shippingFee;
        holder.subtotalDetail.setText(String.format("%,.0fđ", subtotal));
        holder.shippingFeeDetail.setText(String.format("%,.0fđ", order.shippingFee));

        String shippingInfo = order.recipientName + "\n" + order.shippingPhone + "\n" + order.shippingAddress;
        holder.shippingInfo.setText(shippingInfo);

        // Sửa lỗi: Tính tổng số lượng thực tế từ bảng nối
        int totalQuantity = 0;
        if (orderWithProducts.crossRefs != null) {
            for (OrderProductCrossRef ref : orderWithProducts.crossRefs) {
                totalQuantity += ref.quantity;
            }
        }
        holder.itemCount.setText(totalQuantity + " sản phẩm");

        holder.totalAmount.setText(String.format("%,.0fđ", order.getTotalAmount()));
        holder.status.setText(order.getStatus());

        switch (order.getStatus()) {
            case "Đã giao": holder.status.setTextColor(Color.parseColor("#4CAF50")); break;
            case "Đã hủy": holder.status.setTextColor(Color.parseColor("#F44336")); break;
            default: holder.status.setTextColor(Color.parseColor("#FF9800")); break;
        }

        // Sửa lỗi: Thiết lập RecyclerView con với đầy đủ thông tin sản phẩm và số lượng
        holder.innerProductsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (orderWithProducts.products != null && orderWithProducts.crossRefs != null) {
            OrderDetailProductAdapter detailAdapter = new OrderDetailProductAdapter(
                    new ArrayList<>(orderWithProducts.products),
                    new ArrayList<>(orderWithProducts.crossRefs)
            );
            holder.innerProductsRecyclerView.setAdapter(detailAdapter);
        }

        holder.orderHeaderLayout.setOnClickListener(v -> {
            boolean isVisible = holder.detailSectionLayout.getVisibility() == View.VISIBLE;
            holder.detailSectionLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            if (onItemClickListener != null && !isVisible) {
                onItemClickListener.onItemClick(orderWithProducts);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<OrderWithProducts> newOrderList) {
        this.orderList.clear();
        this.orderList.addAll(newOrderList);
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, itemCount, totalAmount, status;
        ConstraintLayout orderHeaderLayout;
        LinearLayout detailSectionLayout;
        RecyclerView innerProductsRecyclerView;
        TextView subtotalDetail, shippingFeeDetail;
        TextView shippingInfo;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderIdTextView);
            orderDate = itemView.findViewById(R.id.orderDateTextView);
            itemCount = itemView.findViewById(R.id.orderItemCountTextView);
            totalAmount = itemView.findViewById(R.id.orderTotalTextView);
            status = itemView.findViewById(R.id.orderStatusTextView);
            orderHeaderLayout = itemView.findViewById(R.id.orderHeaderLayout);
            detailSectionLayout = itemView.findViewById(R.id.detailSectionLayout);
            innerProductsRecyclerView = itemView.findViewById(R.id.innerProductsRecyclerView);
            subtotalDetail = itemView.findViewById(R.id.subtotalDetailTextView);
            shippingFeeDetail = itemView.findViewById(R.id.shippingFeeDetailTextView);
            shippingInfo = itemView.findViewById(R.id.shippingInfoTextView);
        }
    }
}