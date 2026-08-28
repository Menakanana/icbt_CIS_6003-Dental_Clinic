package com.dentalclinic;

import com.dentalclinic.entity.ClinicSetting;
import com.dentalclinic.repository.ClinicSettingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ClinicSettingRepositoryTest {

    @Autowired
    private ClinicSettingRepository clinicSettingRepository;

    @Test
    @DisplayName("Scenario 1: Save and find clinic setting by key")
    public void testSaveAndFindByKey() {
        ClinicSetting setting = new ClinicSetting("clinic_name", "Sunrise Dental Clinic", "Main Name");
        clinicSettingRepository.save(setting);

        Optional<ClinicSetting> found = clinicSettingRepository.findBySettingKey("clinic_name");

        assertTrue(found.isPresent());
        assertEquals("Sunrise Dental Clinic", found.get().getSettingValue());
    }

    @Test
    @DisplayName("Scenario 2: Non-existent key returns Optional.empty()")
    public void testFindByKey_NotFound() {
        Optional<ClinicSetting> found = clinicSettingRepository.findBySettingKey("non_existent_key");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Scenario 3: Update setting value")
    public void testUpdateSetting() {
        ClinicSetting setting = new ClinicSetting("clinic_phone", "011-2223334", "Phone Number");
        ClinicSetting saved = clinicSettingRepository.save(setting);

        saved.setSettingValue("077-9998887");
        ClinicSetting updated = clinicSettingRepository.save(saved);

        assertEquals("077-9998887", updated.getSettingValue());
    }

    @Test
    @DisplayName("Scenario 4: Entity constructors and setters")
    public void testClinicSettingEntity() {
        ClinicSetting s = new ClinicSetting();
        s.setSettingId(10);
        s.setSettingKey("vat_rate");
        s.setSettingValue("15%");
        s.setDescription("Tax percentage");

        assertEquals(10, s.getSettingId());
        assertEquals("vat_rate", s.getSettingKey());
        assertEquals("15%", s.getSettingValue());
        assertEquals("Tax percentage", s.getDescription());
    }
}
