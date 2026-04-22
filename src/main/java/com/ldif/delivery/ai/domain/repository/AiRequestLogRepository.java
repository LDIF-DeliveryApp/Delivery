package com.ldif.delivery.ai.domain.repository;

import com.ldif.delivery.ai.domain.entity.AiRequestLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRequestLogRepository extends JpaRepository<AiRequestLogEntity, Long> {
}
