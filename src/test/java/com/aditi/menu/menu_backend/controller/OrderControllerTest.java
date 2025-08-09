package com.aditi.menu.menu_backend.controller;

import com.aditi.menu.menu_backend.dto.OrderItemRequestDto;
import com.aditi.menu.menu_backend.dto.OrderRequestDto;
import com.aditi.menu.menu_backend.dto.OrderResponseDto;
import com.aditi.menu.menu_backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateOrderWithMultipleItems() throws Exception {
        // Arrange
        OrderItemRequestDto item1 = new OrderItemRequestDto();
        item1.setMenuItemId(1);
        item1.setQuantity(2);

        OrderItemRequestDto item2 = new OrderItemRequestDto();
        item2.setMenuItemId(2);
        item2.setQuantity(1);

        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setQrToken("test-token");
        orderRequestDto.setItems(Arrays.asList(item1, item2));

        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);
        orderResponseDto.setTotalCents(5000); // Assuming item 1 is 2000 cents and item 2 is 1000 cents

        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalCents").value(5000));
    }
}
