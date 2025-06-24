package com.example.project_prm392.model;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "order_product_cross_ref", primaryKeys = {"orderId", "productId"},
        indices = {@Index("productId")})
public class OrderProductCrossRef {
    public long orderId;
    public int productId;
    public int quantity;

    public OrderProductCrossRef(long orderId, int productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
