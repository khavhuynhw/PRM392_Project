package com.example.project_prm392.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int userId;

    public String fullName;
    public String email;
    public String password;
    public String role; // "admin" or "user"
}
