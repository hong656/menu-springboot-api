package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.entity.WebSetting;
import com.aditi.menu.menu_backend.repository.WebSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WebSettingService {

    @Autowired
    private WebSettingRepository webSettingRepository;

    private final Path rootLocation = Paths.get("uploads/images");

    public WebSettingService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

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

    public WebSetting updateLogo(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }

            WebSetting logoSetting = webSettingRepository.findById("logo")
                    .orElseThrow(() -> new RuntimeException("Setting not found with key: logo"));
            final String oldLogoPath = logoSetting.getSettingValue();

            // Create a unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destinationFile = this.rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            // Save the file
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Update the database
            logoSetting.setSettingValue("/uploads/images/" + filename);
            WebSetting updatedWebSetting = webSettingRepository.save(logoSetting);

            // Delete the old file
            if (oldLogoPath != null && !oldLogoPath.isEmpty()) {
                try {
                    String oldFilename = oldLogoPath.substring(oldLogoPath.lastIndexOf('/') + 1);
                    Path oldFilePath = this.rootLocation.resolve(oldFilename).normalize().toAbsolutePath();
                    Files.deleteIfExists(oldFilePath);
                } catch (Exception e) {
                    System.err.println("Failed to delete old file: " + oldLogoPath + " with error: " + e.getMessage());
                }
            }

            return updatedWebSetting;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
