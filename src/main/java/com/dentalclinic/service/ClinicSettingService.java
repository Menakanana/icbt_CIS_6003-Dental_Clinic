package com.dentalclinic.service;

import com.dentalclinic.entity.ClinicSetting;
import com.dentalclinic.repository.ClinicSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service managing dynamic system configuration settings and clinic profile values.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class ClinicSettingService {

    private static final Logger logger = LoggerFactory.getLogger(ClinicSettingService.class);

    private final ClinicSettingRepository settingRepository;

    public static final String KEY_CLINIC_NAME = "clinic_name";
    public static final String KEY_CLINIC_ADDRESS = "clinic_address";
    public static final String KEY_CLINIC_PHONE = "clinic_phone";
    public static final String KEY_CLINIC_CHARGE = "clinic_charge";

    @Autowired
    public ClinicSettingService(ClinicSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * Gets a setting value by key, returning fallback default if not found.
     */
    public String getSettingValue(String key, String defaultValue) {
        return settingRepository.findBySettingKey(key)
                .map(ClinicSetting::getSettingValue)
                .orElse(defaultValue);
    }

    /**
     * Retrieves the current static clinic facility charge from DB (default LKR 500.00).
     */
    public BigDecimal getClinicCharge() {
        String val = getSettingValue(KEY_CLINIC_CHARGE, "500.00");
        try {
            return new BigDecimal(val);
        } catch (Exception ex) {
            return new BigDecimal("500.00");
        }
    }

    /**
     * Saves or updates a setting key-value pair.
     */
    @Transactional
    public void saveSetting(String key, String value, String description) {
        ClinicSetting setting = settingRepository.findBySettingKey(key)
                .orElseGet(() -> new ClinicSetting(key, value, description));
        setting.setSettingValue(value);
        if (description != null) {
            setting.setDescription(description);
        }
        settingRepository.save(setting);
        logger.info("[SETTING-SERVICE] Updated setting key '{}' = '{}'", key, value);
    }

    /**
     * Saves full clinic profile and static facility charge.
     */
    @Transactional
    public void saveClinicProfile(String name, String address, String phone, BigDecimal clinicCharge) {
        if (clinicCharge != null && clinicCharge.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Static Clinic Charge cannot be negative.");
        }
        saveSetting(KEY_CLINIC_NAME, (name != null && !name.isBlank()) ? name : "Sunrise Dental Clinic", "Official Clinic Name");
        saveSetting(KEY_CLINIC_ADDRESS, (address != null && !address.isBlank()) ? address : "123 Galle Road, Colombo 03", "Clinic Address");
        saveSetting(KEY_CLINIC_PHONE, (phone != null && !phone.isBlank()) ? phone : "011-2345678 / 077-1234567", "Clinic Phone Numbers");
        saveSetting(KEY_CLINIC_CHARGE, clinicCharge != null ? clinicCharge.toPlainString() : "500.00", "Static Clinic Facility Charge");
    }
}
