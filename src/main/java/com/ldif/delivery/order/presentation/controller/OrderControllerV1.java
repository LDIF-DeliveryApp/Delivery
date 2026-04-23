package com.ldif.delivery.order.presentation.controller;

import com.ldif.delivery.order.application.service.OrderServiceV1;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;

    private static final Set<Integer> ALLOWED_SIZE = Set.of(10, 30, 50);

    // ───────────────────────────────────────────────────────────
    // 타입: POST
    // URL: /api/orders
    // 주문 생성 (CUSTOMER)
    // ───────────────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ){

        String customerId = userDetails.getUsername();
        OrderResponse response = orderService.createOrder(req, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ───────────────────────────────────────────────────────────
    // 타입: GET
    // URL: /api/orders
    // 주문 목록 조회
    // ───────────────────────────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(
            @RequestParam(required = false) UUID storeId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,DESC") String sort,
            @AuthenticationPrincipal UserDetails userDetails) {

        // size 허용값 검증 (10/30/50만 허용)
        if(!ALLOWED_SIZE.contains(size)) size = 10;

        String[] sortParts = sort.split(",");
        Sort pageSort = Sort.by(
                Sort.Direction.fromString(sortParts.length > 1 ? sortParts[1] : "DESC"), sortParts[0]
        );

        PageRequest pageable = PageRequest.of(page, size, pageSort);

        OrderStatus orderStatus = null;
        if (status != null && !status.isBlank()) {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        }

        String role = extractRole(userDetails);
        PageResponse<OrderResponse> response = orderService.getOrder(
                userDetails.getUsername(), role, storeId, orderStatus, pageable);

        return ResponseEntity.ok(response);
    }

    // ───────────────────────────────────────────────────────────
    // 타입: GET
    // URL: /api/orders/{orderId}
    // 주문 상세 조회
    // ───────────────────────────────────────────────────────────
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String role = extractRole(userDetails);

        return ResponseEntity.ok(
                orderService.getOrder(orderId, userDetails.getUsername(), role));
    }

    // ───────────────────────────────────────────────────────────
    // 타입: PUT
    // URL: /api/orders/{orderId}
    // 주문 요청사항 수정 (CUSTOMER - PENDING만 허용)
    // ──────────────────────────────────────────────────────────
    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MASTER')")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderUpdateRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String role = extractRole(userDetails);
        return ResponseEntity.ok(
                orderService.updateOrder(orderId, req, userDetails.getUsername(), role));
    }

    // ───────────────────────────────────────────────────────────
    // 타입: PATCH
    // URL: /api/orders/{orderId}/status
    // 주문 상태 변경
    // ──────────────────────────────────────────────────────────
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<OrderResponse> changeStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderStatusRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String role = extractRole(userDetails);
        return ResponseEntity.ok(
                orderService.changeStatus(orderId, req, userDetails.getUsername(), role));
    }

    // ───────────────────────────────────────────────────────────
    // 타입: PATCH
    // URL: /api/orders/{orderId}/cancel
    // 주문 취소
    // ──────────────────────────────────────────────────────────
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MASTER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String role = extractRole(userDetails);
        return ResponseEntity.ok(
                orderService.cancelOrder(orderId, userDetails.getUsername(), role));
    }

    // ───────────────────────────────────────────────────────────
    // 타입: DELETE
    // URL: /api/orders/{orderId}
    // 주문 소프트 삭제 (MASTER)
    // ──────────────────────────────────────────────────────────
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('MASTER')")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {

        orderService.deleteOrder(orderId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // 역할 추출
    private String extractRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElseThrow(() -> new SecurityException("권한 정보가 없습니다."));
    }

}