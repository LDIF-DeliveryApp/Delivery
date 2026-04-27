package com.ldif.delivery.order.domain.repository;

import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.order.domain.entity.OrderStatus;
import com.ldif.delivery.order.domain.entity.QOrderEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderEntity> searchOrders(
            String customerId,
            UUID storeId,
            OrderStatus status,
            Pageable pageable
    ) {
        QOrderEntity order = QOrderEntity.orderEntity;
        BooleanBuilder builder = new BooleanBuilder();

        // 소프트 삭제 제외
        builder.and(order.deletedAt.isNull());

        // 동적 필터
        if (customerId != null) {
            builder.and(order.customer.username.eq(customerId));
        }
        if (storeId != null) {
            builder.and(order.store.storeId.eq(storeId));
        }
        if (status != null) {
            builder.and(order.status.eq(status));
        }

        List<OrderEntity> content = queryFactory
                .selectFrom(order)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(order.count())
                .from(order)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
