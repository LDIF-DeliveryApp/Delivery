package com.ldif.delivery.order.domain.entity;

import com.ldif.delivery.address.entity.Address;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.order.exception.OrderBusinessException;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.*;

public class OrderEntityTest {

    // 공통 Mock Entity
    private UserEntity  mockCustomer;
    private StoreEntity mockStore;
    private Address     mockAddress;
    private MenuEntity  mockMenu1;
    private MenuEntity  mockMenu2;

    private OrderEntity order;

    @BeforeEach
    void setUp() {
        // Mock Entity 생성
        mockCustomer = mock(UserEntity.class);
        given(mockCustomer.getUsername()).willReturn("customer1");

        mockAddress = mock(Address.class);
        given(mockAddress.getAddressId()).willReturn(java.util.UUID.randomUUID());

        mockMenu1 = mock(MenuEntity.class);
        given(mockMenu1.getMenuId()).willReturn(java.util.UUID.randomUUID());
        given(mockMenu1.getPrice()).willReturn(10000);

        mockMenu2 = mock(MenuEntity.class);
        given(mockMenu2.getMenuId()).willReturn(java.util.UUID.randomUUID());
        given(mockMenu2.getPrice()).willReturn(5000);

        // OrderItem 생성
        List<OrderItemEntity> items = List.of(
                OrderItemEntity.of(mockMenu1, 2, 10000),
                OrderItemEntity.of(mockMenu2, 1, 5000)
        );

        // Order 생성
        order = OrderEntity.create(
                mockCustomer,
                mockStore,
                mockAddress,
                OrderType.ONLINE,
                "덜 맵게 조리 부탁드립니다.",
                items,
                25000
        );
    }

    // ───────────────────────────────────────────────────────────
    // 주문 생성
    // ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("정상 생성 시 초기 상태는 PENDING")
        void initialStatusIsPending() {
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("주문자 Entity가 올바르게 설정")
        void customerEntitySet() {
            assertThat(order.getCustomer().getUsername()).isEqualTo("customer1");
        }

        @Test
        @DisplayName("주문 항목이 양방향 관계로 연결")
        void itemsAreLinkedToOrder() {
            assertThat(order.getOrderItems()).hasSize(2);
            order.getOrderItems().forEach(item ->
                    assertThat(item.getOrder()).isEqualTo(order));
        }

        @Test
        @DisplayName("주문 항목의 MenuEntity가 올바르게 설정")
        void itemMenuEntityIsSet() {
            assertThat(order.getOrderItems()).extracting(
                    item -> item.getMenu().getMenuId()
            ).containsExactlyInAnyOrder(
                    mockMenu1.getMenuId(),
                    mockMenu2.getMenuId()
            );
        }

        @Test
        @DisplayName("총 금액이 올바르게 저장")
        void totalPriceIsCorrect() {
            assertThat(order.getTotalPrice()).isEqualTo(25000);
        }
    }

    // ───────────────────────────────────────────────────────────
    // 요청사항 수정
    // ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("요청사항 수정 (updateRequest)")
    class UpdateRequest {

        @Test
        @DisplayName("PENDING 상태에서 요청사항을 수정 성공")
        void successWhenPending() {
            order.updateRequest("많이 맵게 조리 부탁드립니다.");
            assertThat(order.getRequest()).isEqualTo("많이 맵게 조리 부탁드립니다.");
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 요청사항 수정 시 예외 발생")
        void failWhenNotPending() {
            order.changeStatus(OrderStatus.ACCEPTED);

            assertThatThrownBy(() -> order.updateRequest("수정 시도"))
                    .isInstanceOf(OrderBusinessException.class)
                    .hasMessageContaining("PENDING");
        }
    }

    // ───────────────────────────────────────────────────────────
    // 상태 변경
    // ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("주문 상태 변경 (changeStatus)")
    class ChangeStatus {

        @Test
        @DisplayName("PENDING -> ACCEPTED 정상 변경")
        void pendingToAccepted() {
            order.changeStatus(OrderStatus.ACCEPTED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("ACCEPTED -> COOKING 정상 변경")
        void acceptedToCooking() {
            order.changeStatus(OrderStatus.ACCEPTED);
            order.changeStatus(OrderStatus.COOKING);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.COOKING);
        }

        @Test
        @DisplayName("전체 정상 상태 변경 흐름: PENDING -> ACCEPTED -> COOKING -> DELIVERING -> COMPLETED")
        void successPath() {
            order.changeStatus(OrderStatus.ACCEPTED);
            order.changeStatus(OrderStatus.COOKING);
            order.changeStatus(OrderStatus.DELIVERING);
            order.changeStatus(OrderStatus.COMPLETED);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("PENDING -> COOKING 순서 건너뛰면 예외 발생")
        void failWhenSkipStatus() {
            assertThatThrownBy(() -> order.changeStatus(OrderStatus.COOKING))
                    .isInstanceOf(OrderBusinessException.class);
        }

        @Test
        @DisplayName("COMPLETED 이후 상태 변경하면 예외 발생")
        void failWhenAlreadyDelivered() {
            order.changeStatus(OrderStatus.ACCEPTED);
            order.changeStatus(OrderStatus.COOKING);
            order.changeStatus(OrderStatus.DELIVERING);
            order.changeStatus(OrderStatus.COMPLETED);

            assertThatThrownBy(() -> order.changeStatus(OrderStatus.COOKING))
                    .isInstanceOf(OrderBusinessException.class);
        }

        @Test
        @DisplayName("PENDING -> CANCELLED 정상 변경")
        void pendingToCancelled() {
            order.changeStatus(OrderStatus.CANCELED);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        }

        @Test
        @DisplayName("CANCELLED 이후 상태 변경 시 예외 발생")
        void failWhenAlreadyCancelled() {
            order.changeStatus(OrderStatus.CANCELED);

            assertThatThrownBy(() -> order.changeStatus(OrderStatus.ACCEPTED))
                    .isInstanceOf(OrderBusinessException.class);
        }
    }

    // ───────────────────────────────────────────────────────────
    // 소프트 삭제
    // ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("소프트 삭제 (softDelete)")
    class SoftDelete {

        @Test
        @DisplayName("softDelete 호출 시 deletedAt, deletedBy 설정")
        void softDeleteSetsFields() {
            order.softDelete("master_user");

            assertThat(order.getDeletedAt()).isNotNull();
            assertThat(order.getDeletedBy()).isEqualTo("master_user");
        }

        @Test
        @DisplayName("삭제 전 deletedAt은 null")
        void deletedAtIsNearNow() {
            order.softDelete("master_user");

            assertThat(order.getDeletedAt())
                    .isAfter(java.time.LocalDateTime.now().minusSeconds(5))
                    .isBefore(java.time.LocalDateTime.now().plusSeconds(5));
        }
    }
}