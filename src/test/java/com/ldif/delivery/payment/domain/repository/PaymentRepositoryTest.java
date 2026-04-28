package com.ldif.delivery.payment.domain.repository;

import com.ldif.delivery.area.domain.entity.AreaEntity;
import com.ldif.delivery.area.persentation.dto.AreaRequest;
import com.ldif.delivery.category.domain.entity.CategoryEntity;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderType;
import com.ldif.delivery.payment.domain.entity.PaymentEntity;
import com.ldif.delivery.payment.domain.entity.PaymentStatus;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import com.ldif.delivery.user.domain.entity.UserRoleEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
@Import(PaymentRepositoryTest.QuerydslTestConfig.class)
class PaymentRepositoryTest {

    @TestConfiguration
    static class QuerydslTestConfig {

        @PersistenceContext
        private EntityManager entityManager;

        @Bean
        JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(entityManager);
        }
    }

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findAllByDeletedAtIsNull_returnsOnlyNotDeletedPayments() {
        OrderEntity order1 = createOrder();
        OrderEntity order2 = createOrder();

        PaymentEntity payment = new PaymentEntity(order1, 10000);

        PaymentEntity deletedPayment = new PaymentEntity(order2, 20000);
        deletedPayment.softDelete("testUser");

        em.persist(payment);
        em.persist(deletedPayment);
        em.flush();
        em.clear();

        Page<PaymentEntity> result =
                paymentRepository.findAllByDeletedAtIsNull(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAmount()).isEqualTo(10000);
    }

    @Test
    void findByStatusAndDeletedAtIsNull_returnsMatchingStatusOnly() {
        OrderEntity order1 = createOrder();
        OrderEntity order2 = createOrder();

        PaymentEntity pendingPayment = new PaymentEntity(order1, 10000);

        PaymentEntity cancelledPayment = new PaymentEntity(order2, 20000);
        cancelledPayment.cancel();

        em.persist(pendingPayment);
        em.persist(cancelledPayment);
        em.flush();
        em.clear();

        Page<PaymentEntity> result =
                paymentRepository.findByStatusAndDeletedAtIsNull(
                        PaymentStatus.PENDING,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus())
                .isEqualTo(PaymentStatus.PENDING);
    }

    private OrderEntity createOrder() {
        UserEntity customer = createUser("user" + UUID.randomUUID().toString().substring(0, 4), UserRoleEnum.CUSTOMER);
        UserEntity owner = createUser("own" + UUID.randomUUID().toString().substring(0, 5), UserRoleEnum.OWNER);

        CategoryEntity category = createCategory();
        AreaEntity area = createArea();

        em.persist(customer);
        em.persist(owner);
        em.persist(category);
        em.persist(area);

        StoreEntity store = new StoreEntity(
                owner,
                category,
                area,
                "테스트 가게",
                "서울시 종로구",
                "010-1234-5678"
        );

        em.persist(store);

        OrderEntity order = OrderEntity.create(
                customer,
                store,
                UUID.randomUUID(),
                OrderType.ONLINE,
                "요청사항 없음",
                List.of(),
                10000
        );

        return em.persist(order);
    }

    private UserEntity createUser(String username, UserRoleEnum role) {
        return new UserEntity(
                username,
                username,
                username + "@test.com",
                "password",
                role
        );
    }

    private CategoryEntity createCategory() {
        return new CategoryEntity("한식" + UUID.randomUUID());
    }

    private AreaEntity createArea() {
        AreaRequest request = mock(AreaRequest.class);

        when(request.getName()).thenReturn("서울" + UUID.randomUUID());
        when(request.getCity()).thenReturn("서울시");
        when(request.getDistrict()).thenReturn("종로구");

        return new AreaEntity(request);
    }
}