package com.aditi.menu.menu_backend.service;

import com.aditi.menu.menu_backend.dto.StatusUpdateDto;
import com.aditi.menu.menu_backend.entity.MenuType;
import com.aditi.menu.menu_backend.repository.MenuTypeRepository;
import com.aditi.menu.menu_backend.specs.MenuTypeSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public MenuType createMenuType(MenuType menuType) {
        return menuTypeRepository.save(menuType);
    }

    public MenuType updateMenuType(Integer id, MenuType menuTypeDetails) {
        MenuType existingMenuType = getMenuTypeById(id);
        existingMenuType.setName(menuTypeDetails.getName());
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