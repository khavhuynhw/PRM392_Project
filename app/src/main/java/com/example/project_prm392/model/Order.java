package com.example.project_prm392.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "orders",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "userId",
                childColumns = "buyerUserId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("buyerUserId")})
public class Order {
    @PrimaryKey(autoGenerate = true)
    public long orderId;

    private long orderDate;
    private double totalAmount;
    private String status;
    public int buyerUserId;
    public double shippingFee;

    public String recipientName;
    public String shippingAddress;
    public String shippingPhone;

    @Ignore
    private ArrayList<Product> products;

    // Cập nhật Constructor
    public Order(long orderDate, double totalAmount, String status, int buyerUserId, double shippingFee, String recipientName, String shippingAddress, String shippingPhone) {
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.buyerUserId = buyerUserId;
        this.shippingFee = shippingFee;
        this.recipientName = recipientName;
        this.shippingAddress = shippingAddress;
        this.shippingPhone = shippingPhone;
    }

    // Getters
    public long getOrderId() { return orderId; }
    public long getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public ArrayList<Product> getProducts() { return products; }
    public int getBuyerUserId() { return buyerUserId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setProducts(ArrayList<Product> products) { this.products = products; }
    public void setBuyerUserId(int buyerUserId) { this.buyerUserId = buyerUserId; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public int getItemCount() {
        return products != null ? products.size() : 0;
    }
}