package com.example.project_prm392.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CartItemWithProduct {
    @Embedded
    public CartItem cartItem;

    @Relation(
            parentColumn = "productId",
            entityColumn = "productId"
    )
    public Product product;
}