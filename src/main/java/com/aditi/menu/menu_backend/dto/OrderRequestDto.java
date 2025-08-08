package com.aditi.menu.menu_backend.dto;

import java.util.List;

public class OrderRequestDto {
    private String qrToken;
    private String remark;
    private List<OrderItemRequestDto> items;

    // --- ADD THESE GETTERS AND SETTERS ---
    public String getQrToken() { return qrToken; }
    
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }
}
