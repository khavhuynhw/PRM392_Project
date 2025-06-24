package com.example.project_prm392.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class OrderWithProducts {
    @Embedded
    public Order order;
    @Relation(
            parentColumn = "orderId",
            entityColumn = "productId",
            associateBy = @Junction(OrderProductCrossRef.class)
    )
    public List<Product> products;

    @Relation(
            parentColumn = "orderId",
            entityColumn = "orderId",
            entity = OrderProductCrossRef.class
    )
    public List<OrderProductCrossRef> crossRefs;
}
