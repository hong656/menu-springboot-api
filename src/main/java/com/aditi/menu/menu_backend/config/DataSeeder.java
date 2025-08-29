package com.aditi.menu.menu_backend.config;

import com.aditi.menu.menu_backend.entity.WebSetting;
import com.aditi.menu.menu_backend.repository.WebSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private WebSettingRepository webSettingRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Seeding initial web settings data...");
        seedWebSettings();
        System.out.println("Data seeding complete.");
    }

    private void seedWebSettings() {
        if (webSettingRepository.count() == 0) {

            WebSetting logo = new WebSetting("logo", "/uploads/default_logo.png");
            WebSetting title = new WebSetting("title", "Aditi's Restaurant Menu");
            WebSetting mainTheme = new WebSetting("main_theme", "light");

            List<WebSetting> initialSettings = Arrays.asList(logo, title, mainTheme);

            webSettingRepository.saveAll(initialSettings);

            System.out.println("Initial web settings have been seeded to the database.");
        } else {
            System.out.println("Web settings data already exists. Seeding skipped.");
        }
    }
}