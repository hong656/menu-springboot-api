package com.aditi.menu.menu_backend.dto;

import java.util.List;

public class MenuItemRequestDto {
    private Integer priceCents;
    private Integer status;
    private Integer menuTypeId;
    private List<MenuItemTranslationDto> translations;

    public Integer getPriceCents() { return priceCents; }
    public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getMenuTypeId() { return menuTypeId; }
    public void setMenuTypeId(Integer menuTypeId) { this.menuTypeId = menuTypeId; }
    public List<MenuItemTranslationDto> getTranslations() { return translations; }
    public void setTranslations(List<MenuItemTranslationDto> translations) { this.translations = translations; }
}