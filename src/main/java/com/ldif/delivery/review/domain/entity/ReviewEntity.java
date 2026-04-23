package com.ldif.delivery.review.domain.entity;

import com.ldif.delivery.global.infrastructure.entity.BaseEntity;
import com.ldif.delivery.order.domain.entity.OrderEntity;
import com.ldif.delivery.store.domain.entity.StoreEntity;
import com.ldif.delivery.user.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    private UUID reviewId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    public ReviewEntity(OrderEntity order, StoreEntity store, UserEntity user, Integer rating, String content){

        this.order = order;
        this.store = store;
        this.user = user;
        this.rating = rating;
        this.content = content;
    }

}
