package com.ldif.delivery.order.domain.repository;

import com.ldif.delivery.address.entity.Address;
import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.global.infrastructure.config.QueryDslConfig;
import com.ldif.delivery.menu.domain.entity.MenuEntity;
import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import com.ldif.delivery.order.domain.entity.*;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslConfig.class)
public class OrderRepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    OrderRepository orderRepository;

    // 공통 Entity 픽스처
    private UserEntity      customer1;
    private UserEntity      customer2;
    private UserEntity      owner;
    private StoreEntity     store1;
    private StoreEntity     store2;
    private MenuEntity      menu1;
    private MenuEntity      menu2;
    private Address         address;
    private CategoryEntity  category;
    private AreaEntity      area;

    @BeforeEach
    void setUp() {
        // User
        customer1 = em.persist(new UserEntity(
                "customer1", "고객1", "customer1@test.com", "pw", UserRoleEnum.CUSTOMER));
        customer2 = em.persist(new UserEntity(
                "customer2", "고객2", "customer2@test.com", "pw", UserRoleEnum.CUSTOMER));
        owner = em.persist(new UserEntity(
                "owner1", "점주1", "owner12@test.com", "pw", UserRoleEnum.OWNER));

        em.flush();

        // Category
        category = em.persist(new CategoryEntity("분식"));

        // Area
        AreaRequest areaRequest = new AreaRequest();
        ReflectionTestUtils.setField(areaRequest, "name",       "광화문");
        ReflectionTestUtils.setField(areaRequest, "city",       "서울특별시");
        ReflectionTestUtils.setField(areaRequest, "district",   "종로구");
        area = em.persist(new AreaEntity(areaRequest));

        em.flush();

        // Store
        store1 = em.persist(new StoreEntity(
                owner, category, area, "가게1", "서울시 종로구 지하문로1가길 11", "010-0000-0000"));
        store2 = em.persist(new StoreEntity(
                owner, category, area, "가게2", "서울시 종로구 지하문로2가길 22", "010-1111-1111"));

        em.flush();

        // Menu
        MenuRequest menuRequest= new MenuRequest();
        ReflectionTestUtils.setField(menuRequest, "name",           "메뉴1");
        ReflectionTestUtils.setField(menuRequest, "price",          5000);
        menu1 = em.persist(new MenuEntity(menuRequest, store1));

        // Address
        address = em.persist(Address.builder()
                .user(customer1).address("서울시 종로구 지하문로3가길 33")).build();

        em.flush();
        em.clear();
    }

    // 주문 생성 헬퍼
    private OrderEntity createOrder(UserEntity customer, StoreEntity store, OrderStatus status) {
        List<OrderItemEntity> items = List.of(
                OrderItemEntity.of(menu1, 1, menu1.getPrice())
        );

        OrderEntity order = OrderEntity.create(
                customer,
                store,
                address,
                OrderType.ONLINE,
                "요청사항",
                items,
                menu1.getPrice()
        );

        // status 변경이 필요한 경우
        if (status != OrderStatus.PENDING) {
            order.changeStatus(OrderStatus.ACCEPTED);
            if (status == OrderStatus.COOKING) order.changeStatus(OrderStatus.COOKING);
        }

        return em.persist(order);
    }

    // ───────────────────────────────────────────────────────────
    // findActiveById - 소프트 삭제 필터
    // ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("findActiveById")
    class FindActiveById {

        @Test
        @DisplayName("삭제되지 않은 주문 조회 성공")
        void findActiveOrder() {
            OrderEntity order = createOrder(customer1, store1, OrderStatus.PENDING);
            em.flush(); em.clear();

            Optional<OrderEntity> result = orderRepository.findActiveById(order.getOrderId());

            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("소프트 삭제된 주문은 조회되지 않는다.")
        void findDeletedOrder() {
            OrderEntity order = createOrder(customer1, store1, OrderStatus.PENDING);
            order.softDelete("master1");
            em.persist(order);
            em.flush(); em.clear();

            Optional<OrderEntity> result = orderRepository.findActiveById(order.getOrderId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 empty 반환")
        void findNotExistId() {
            Optional<OrderEntity> result = orderRepository.findActiveById(UUID.randomUUID());

            assertThat(result).isEmpty();
        }
    }

    // ───────────────────────────────────────────────────────────
    // searchOrders - QueryDSL 동적 쿼리
    // ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("searchOrders")
    class SearchOrders {

        private PageRequest defaultPage() {
            return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        @Test
        @DisplayName("필터 없이 전체 주문 조회 (소프트 삭제 제외)")
        void findAllWithoutFilter() {
            createOrder(customer1, store1, OrderStatus.PENDING);
            createOrder(customer2, store1, OrderStatus.PENDING);
            createOrder(customer1, store2, OrderStatus.PENDING);
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    null, null, null, defaultPage());

            assertThat(result.getTotalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("customerId 필터: 본인 주문만 조회")
        void filterByCustomerId() {
            createOrder(customer1, store1, OrderStatus.PENDING);
            createOrder(customer2, store1, OrderStatus.PENDING);
            createOrder(customer1, store2, OrderStatus.PENDING);
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    "customer1", null, null, defaultPage());

            assertThat(result.getTotalElements()).isEqualTo(2);
            result.getContent().forEach(order ->
                    assertThat(order.getCustomer().getUsername()).isEqualTo("customer1")
            );
        }

        @Test
        @DisplayName("storeId 필터: 해당 가게 주문만 조회")
        void filterByStoreId() {
            createOrder(customer1, store1, OrderStatus.PENDING);
            createOrder(customer2, store1, OrderStatus.PENDING);
            createOrder(customer1, store2, OrderStatus.PENDING);
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    null, store1.getStoreId(), null, defaultPage());

            assertThat(result.getTotalElements()).isEqualTo(2);
            result.getContent().forEach(order ->
                    assertThat(order.getStore().getStoreId()).isEqualTo(store1.getStoreId())
            );
        }

        @Test
        @DisplayName("status 필터: 해당 상태 주문만 조회")
        void filterByStatus() {
            createOrder(customer1, store1, OrderStatus.PENDING);
            createOrder(customer1, store1, OrderStatus.ACCEPTED);
            createOrder(customer1, store1, OrderStatus.ACCEPTED);
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    null, null, OrderStatus.ACCEPTED, defaultPage());

            assertThat(result.getTotalElements()).isEqualTo(2);
            result.getContent().forEach(o ->
                    assertThat(o.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
            );
        }

        @Test
        @DisplayName("customerId + storeId 복합 필터")
        void filterByCustomerIdAndStoreId() {
            createOrder(customer1, store1, OrderStatus.PENDING); // 해당
            createOrder(customer1, store2, OrderStatus.PENDING); // storeId 다름
            createOrder(customer2, store1, OrderStatus.PENDING); // customerId 다름
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    "customer1", store1.getStoreId(), null, defaultPage());

            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("소프트 삭제된 주문은 목록에서 제외")
        void excludeSoftDeletedOrders() {
            OrderEntity activeOrder  = createOrder(customer1, store1, OrderStatus.PENDING);
            OrderEntity deletedOrder = createOrder(customer1, store1, OrderStatus.PENDING);
            deletedOrder.softDelete("master1");
            em.persist(deletedOrder);
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    "customer1", null, null, defaultPage());

            assertThat(result.getTotalElements()).isEqualTo(1); // 삭제된 것 제외
            assertThat(result.getContent().get(0).getOrderId())
                    .isEqualTo(activeOrder.getOrderId());
        }

        @Test
        @DisplayName("페이지네이션: size 10 기준 2페이지")
        void pagination() {
            for (int i = 0; i < 15; i++) {
                createOrder(customer1, store1, OrderStatus.PENDING);
            }
            em.flush(); em.clear();

            PageRequest page0 = PageRequest.of(0, 10,
                    Sort.by(Sort.Direction.DESC, "createdAt"));
            PageRequest page1 = PageRequest.of(1, 10,
                    Sort.by(Sort.Direction.DESC, "createdAt"));

            Page<OrderEntity> result0 = orderRepository.searchOrders(
                    null, null, null, page0);
            Page<OrderEntity> result1 = orderRepository.searchOrders(
                    null, null, null, page1);

            assertThat(result0.getContent()).hasSize(10);
            assertThat(result1.getContent()).hasSize(5);
            assertThat(result0.getTotalElements()).isEqualTo(15);
            assertThat(result0.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("정렬: createdAt DESC 기준 최신순 정렬")
        void sortByCreatedAtDesc() {
            createOrder(customer1, store1, OrderStatus.PENDING);
            createOrder(customer1, store1, OrderStatus.PENDING);
            createOrder(customer1, store1, OrderStatus.PENDING);
            em.flush(); em.clear();

            Page<OrderEntity> result = orderRepository.searchOrders(
                    null, null, null, defaultPage());

            List<OrderEntity> content = result.getContent();
            for (int i = 0; i < content.size() - 1; i++) {
                assertThat(content.get(i).getCreatedAt())
                        .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
            }
        }
    }
}