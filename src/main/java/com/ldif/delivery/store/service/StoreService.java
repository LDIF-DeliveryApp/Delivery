package com.ldif.delivery.store.service;

import com.ldif.delivery.store.dto.StoreRequest;
import com.ldif.delivery.store.dto.StoreResponse;
import com.ldif.delivery.store.entity.StoreEntity;
import com.ldif.delivery.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public UUID createStore(StoreRequest request){
        StoreEntity store = new StoreEntity(
                request.getName(),
                request.getAddress(),
                request.getPhone()
        );

        return storeRepository.save(store).getStoreId();
    }

    public StoreResponse getStore(UUID storeId){
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if(store.isDeleted()){
            throw new IllegalArgumentException("삭제된 가게입니다.");
        }

        return new StoreResponse(store);
    }

    @Transactional
    public void updateStore(UUID storeId, StoreRequest request){
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()){
            throw new IllegalArgumentException("삭제된 가게는 수정할 수 없습니다.");
        }

        store.updateStore(
                request.getName(),
                request.getAddress(),
                request.getPhone()
        );
    }

    @Transactional
    public void deleteStore(UUID storeId){
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()){
            throw new IllegalArgumentException("이미 삭제된 가게입니다.");
        }
    }
}
