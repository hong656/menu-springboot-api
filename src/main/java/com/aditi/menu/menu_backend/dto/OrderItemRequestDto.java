package com.aditi.menu.menu_backend.dto;

public class OrderItemRequestDto {
    private Integer menuItemId;
    private int quantity;

    // --- ADD THESE GETTERS AND SETTERS ---
    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
