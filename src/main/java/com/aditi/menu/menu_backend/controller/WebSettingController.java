package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.WebSettingsUpdateRequestDto;
import com.aditi.menu.menu_backend.entity.WebSetting;
import com.aditi.menu.menu_backend.service.WebSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/web-settings")
public class WebSettingController {

    @Autowired
    private WebSettingService webSettingService;

    @PutMapping
    public ResponseEntity<List<WebSetting>> updateWebSettings(@RequestBody WebSettingsUpdateRequestDto request) {
        List<WebSetting> updatedSettings = webSettingService.updateWebSettings(request.getSettings());
        return ResponseEntity.ok(updatedSettings);
    }

    @GetMapping
    public ResponseEntity<List<WebSetting>> getWebSettings() {
        return ResponseEntity.ok(webSettingService.getWebSettings());
    }

    @PostMapping
    public ResponseEntity<WebSetting> updateLogo(@RequestParam("logo") MultipartFile file) {
        WebSetting updatedLogo = webSettingService.updateLogo(file);
        return ResponseEntity.ok(updatedLogo);
    }
}
