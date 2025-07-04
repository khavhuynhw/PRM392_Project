package com.example.project_prm392.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "products",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "categoryId",
                childColumns = "productCategoryId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("productCategoryId")})
public class Product implements Parcelable { // <-- Implement Parcelable
    @PrimaryKey(autoGenerate = true)
    public int productId;

    private String name;
    private double price;
    private String unit;
    private int imageResourceId;
    public int productCategoryId;

    public Product(String name, double price, String unit, int imageResourceId, int productCategoryId) {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.imageResourceId = imageResourceId;
        this.productCategoryId = productCategoryId;
    }

    // --- Parcelable Implementation ---
    protected Product(Parcel in) {
        productId = in.readInt();
        name = in.readString();
        price = in.readDouble();
        unit = in.readString();
        imageResourceId = in.readInt();
        productCategoryId = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(unit);
        dest.writeInt(imageResourceId);
        dest.writeInt(productCategoryId);
    }

    // --- Getters and Setters giữ nguyên ---
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getUnit() { return unit; }
    public int getImageResourceId() { return imageResourceId; }
    public int getProductCategoryId() { return productCategoryId; }

    // Add setters for admin functionality
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }
    public void setProductCategoryId(int productCategoryId) { this.productCategoryId = productCategoryId; }

}