package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.OrderRequestDto;
import com.aditi.menu.menu_backend.dto.OrderResponseDto;
import com.aditi.menu.menu_backend.entity.Banner;
import com.aditi.menu.menu_backend.entity.MenuItem;
import com.aditi.menu.menu_backend.entity.WebSetting;
import com.aditi.menu.menu_backend.service.MenuItemService;
import com.aditi.menu.menu_backend.service.OrderService;
import com.aditi.menu.menu_backend.service.WebSettingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aditi.menu.menu_backend.service.BannerService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private WebSettingService webSettingService;

    @GetMapping("/menu-items")
    public List<MenuItem> getAllPublicMenuItems() {
        return menuItemService.getAllPublicMenuItems();
    }

    @GetMapping("/banners")
    public List<Banner> getAllPublicBanners() {
        return bannerService.getAllPublicBanners();
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/web-settings")
    public ResponseEntity<List<WebSetting>> getWebSettings() {
        return ResponseEntity.ok(webSettingService.getWebSettings());
    }
}
