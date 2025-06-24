package com.example.project_prm392.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "addresses",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "userId",
                childColumns = "ownerUserId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("ownerUserId")})
public class Address implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int addressId;

    private String recipientName;
    private String phone;
    private String streetAddress;
    private String city;
    private boolean isDefault;
    public int ownerUserId;

    public Address(String recipientName, String phone, String streetAddress, String city, boolean isDefault, int ownerUserId) {
        this.recipientName = recipientName;
        this.phone = phone;
        this.streetAddress = streetAddress;
        this.city = city;
        this.isDefault = isDefault;
        this.ownerUserId = ownerUserId;
    }

    // --- BẮT ĐẦU PHẦN THÊM MỚI ---
    protected Address(Parcel in) {
        addressId = in.readInt();
        recipientName = in.readString();
        phone = in.readString();
        streetAddress = in.readString();
        city = in.readString();
        isDefault = in.readByte() != 0;
        ownerUserId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(addressId);
        dest.writeString(recipientName);
        dest.writeString(phone);
        dest.writeString(streetAddress);
        dest.writeString(city);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeInt(ownerUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };
    // --- KẾT THÚC PHẦN THÊM MỚI ---


    // Getters giữ nguyên
    public String getRecipientName() { return recipientName; }
    public String getPhone() { return phone; }
    public String getStreetAddress() { return streetAddress; }
    public String getCity() { return city; }
    public boolean isDefault() { return isDefault; }
    public int getOwnerUserId() { return ownerUserId; }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}

