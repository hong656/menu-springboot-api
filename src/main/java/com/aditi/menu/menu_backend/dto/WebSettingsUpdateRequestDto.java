package com.aditi.menu.menu_backend.dto;

import java.util.Map;

public class WebSettingsUpdateRequestDto {
    private Map<String, String> settings;

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}