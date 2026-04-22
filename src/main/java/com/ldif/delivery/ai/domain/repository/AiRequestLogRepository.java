package com.ldif.delivery.ai.domain.repository;

import com.ldif.delivery.ai.domain.entity.AiRequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRequestLogRepository extends JpaRepository<AiRequestLogEntity, UUID> {
}
