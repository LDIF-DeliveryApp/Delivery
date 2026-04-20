package com.ldif.delivery.store.service;

import com.ldif.delivery.store.dto.StoreRequest;
import com.ldif.delivery.store.dto.StoreResponse;
import com.ldif.delivery.store.entity.StoreEntity;
import com.ldif.delivery.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Long createStore(StoreRequest request, String username){
        StoreEntity store = new StoreEntity(
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                username
        );

        return storeRepository.save(store).getStoreId();
    }

    public StoreResponse getStore(Long storeId){
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if(store.isDeleted()){
            throw new IllegalArgumentException("삭제된 가게입니다.");
        }

        return new StoreResponse(store);
    }

    @Transactional
    public void updateStore(Long storeId, StoreRequest request, String username){
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()){
            throw new IllegalArgumentException("삭제된 가게는 수정할 수 없습니다.");
        }

        store.updateStore(
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                username
        );
    }

    @Transactional
    public void deleteStore(Long storeId, String username){
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (store.isDeleted()){
            throw new IllegalArgumentException("이미 삭제된 가게입니다.");
        }

        store.softDelete(username);
    }
}
