package com.aditi.menu.menu_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "menu_type_translations", uniqueConstraints = {@UniqueConstraint(columnNames = {"menu_type_id", "language_code"})})
public class MenuTypeTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_type_id", nullable = false)
    @JsonBackReference
    private MenuType menuType;

    @Column(name = "language_code", nullable = false, length = 5)
    private String languageCode;

    @Column(nullable = false, length = 100)
    private String name;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public MenuType getMenuType() { return menuType; }
    public void setMenuType(MenuType menuType) { this.menuType = menuType; }
    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
