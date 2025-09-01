package com.aditi.menu.menu_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "menu_item_translations",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"menu_item_id", "language_code"})})
public class MenuItemTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    @JsonBackReference
    private MenuItem menuItem;

    @Column(name = "language_code", nullable = false, length = 5)
    private String languageCode; // e.g., "en", "fr", "kh"

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1024)
    private String description;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}