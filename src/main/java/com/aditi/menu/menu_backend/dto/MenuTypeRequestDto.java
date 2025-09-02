package com.aditi.menu.menu_backend.dto;

import java.util.List;

public class MenuTypeRequestDto {
    private Integer status;
    private List<MenuTypeTranslationDto> translations;

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public List<MenuTypeTranslationDto> getTranslations() { return translations; }
    public void setTranslations(List<MenuTypeTranslationDto> translations) { this.translations = translations; }
}