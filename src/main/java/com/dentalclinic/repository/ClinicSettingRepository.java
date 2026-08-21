package com.dentalclinic.repository;

import com.dentalclinic.entity.ClinicSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ClinicSetting entity operations.
 */
@Repository
public interface ClinicSettingRepository extends JpaRepository<ClinicSetting, Integer> {
    Optional<ClinicSetting> findBySettingKey(String settingKey);
}
