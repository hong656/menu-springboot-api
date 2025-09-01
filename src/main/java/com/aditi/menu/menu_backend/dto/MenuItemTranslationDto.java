package com.aditi.menu.menu_backend.dto;

public class MenuItemTranslationDto {
    private String languageCode;
    private String name;
    private String description;

    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
