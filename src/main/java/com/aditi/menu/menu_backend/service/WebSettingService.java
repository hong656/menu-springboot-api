package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.entity.WebSetting;
import com.aditi.menu.menu_backend.repository.WebSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WebSettingService {

    @Autowired
    private WebSettingRepository webSettingRepository;

    public List<WebSetting> updateWebSettings(Map<String, String> settings) {
        List<WebSetting> updatedSettings = new ArrayList<>();
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            WebSetting webSetting = webSettingRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Setting not found with key: " + entry.getKey()));
            webSetting.setSettingValue(entry.getValue());
            updatedSettings.add(webSettingRepository.save(webSetting));
        }
        return updatedSettings;
    }

    public List<WebSetting> getWebSettings() {
        return webSettingRepository.findAll();
    }
}
