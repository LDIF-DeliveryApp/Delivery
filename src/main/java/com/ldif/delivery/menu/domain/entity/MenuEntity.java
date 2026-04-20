package com.ldif.delivery.menu.domain.entity;

import com.ldif.delivery.menu.presentation.dto.MenuRequest;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "p_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column
    private String description;

    @Column(nullable = false)
    private Boolean isHidden = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

//    @ManyToOne
//    @JoinColumn(name = store_id, nullable = false)
//    private Store store;

    public MenuEntity(String name, Integer price, String description, Boolean isHidden) {
        //this.store=store;
        this.name = name;
        this.price = price;
        this.description = description;
        this.isHidden = isHidden;
    }

    public void update(@Valid MenuRequest request) {
        this.name = request.getName();
        this.price = request.getPrice();
        this.description = request.getDescription();
    }

    public void hide() {
        isHidden = !isHidden;
    }

    public void delete() {
        isDeleted = !isDeleted;
    }
}
