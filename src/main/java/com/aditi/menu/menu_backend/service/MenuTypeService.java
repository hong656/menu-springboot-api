package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.dto.MenuTypeRequestDto;
import com.aditi.menu.menu_backend.dto.MenuTypeTranslationDto;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.entity.MenuTypeTranslation;
import com.aditi.menu.menu_backend.repository.MenuTypeRepository;
import com.aditi.menu.menu_backend.specs.MenuTypeSpecification;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.function.Function;

@Service
public class MenuTypeService {

    @Autowired
    private MenuTypeRepository menuTypeRepository;

    @Autowired
    private MenuTypeSpecification menuTypeSpecification;

    public Page<MenuType> getAllMenuTypes(Pageable pageable, String search, Integer status) {
        Specification<MenuType> spec = menuTypeSpecification.getMenuType(search, status);
        return menuTypeRepository.findAll(spec, pageable);
    }

    public List<MenuType> getAllTypes() {
        return menuTypeRepository.findAllByStatusNotIn(List.of(2, 3));
    }

    public MenuType getMenuTypeById(Integer id) {
        return menuTypeRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("MenuType not found with id: " + id));
    }

    @Transactional
    public MenuType createMenuType(MenuTypeRequestDto menuTypeDto) {
        MenuType menuType = new MenuType();
        menuType.setStatus(menuTypeDto.getStatus());

        List<MenuTypeTranslation> translations = menuTypeDto.getTranslations().stream()
                .map(dto -> {
                    MenuTypeTranslation translation = new MenuTypeTranslation();
                    translation.setLanguageCode(dto.getLanguageCode());
                    translation.setName(dto.getName());
                    translation.setMenuType(menuType);
                    return translation;
                }).collect(Collectors.toList());
        menuType.setTranslations(translations);

        return menuTypeRepository.save(menuType);
    }

    @Transactional
    public MenuType updateMenuType(Integer id, MenuTypeRequestDto menuTypeDto) {
        MenuType existingMenuType = getMenuTypeById(id);
        existingMenuType.setStatus(menuTypeDto.getStatus());

        Map<String, MenuTypeTranslation> existingTranslationsMap = existingMenuType.getTranslations().stream()
                .collect(Collectors.toMap(MenuTypeTranslation::getLanguageCode, Function.identity()));

        for (MenuTypeTranslationDto translationDto : menuTypeDto.getTranslations()) {
            MenuTypeTranslation existingTranslation = existingTranslationsMap.get(translationDto.getLanguageCode());

            if (existingTranslation != null) {
                existingTranslation.setName(translationDto.getName());
                existingTranslationsMap.remove(translationDto.getLanguageCode());
            } else {
                MenuTypeTranslation newTranslation = new MenuTypeTranslation();
                newTranslation.setLanguageCode(translationDto.getLanguageCode());
                newTranslation.setName(translationDto.getName());
                newTranslation.setMenuType(existingMenuType);
                existingMenuType.getTranslations().add(newTranslation);
            }
        }

        if (!existingTranslationsMap.isEmpty()) {
            existingMenuType.getTranslations().removeAll(existingTranslationsMap.values());
        }

        return menuTypeRepository.save(existingMenuType);
    }

    public MenuType updateMenuTypeStatus(Integer id, StatusUpdateDto statusUpdateDto) {
        MenuType menuType = getMenuTypeById(id);
        menuType.setStatus(statusUpdateDto.getStatus());
        return menuTypeRepository.save(menuType);
    }

    public void deleteMenuType(Integer id) {
        if (!menuTypeRepository.existsById(id)) {
            throw new RuntimeException("MenuType not found with id: " + id);
        }
        menuTypeRepository.deleteById(id);
    }
}