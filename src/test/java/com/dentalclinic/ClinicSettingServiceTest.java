package com.dentalclinic;

import com.dentalclinic.dto.TreatmentTypeDTO;
import com.dentalclinic.entity.ClinicSetting;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.repository.ClinicSettingRepository;
import com.dentalclinic.repository.TreatmentTypeRepository;
import com.dentalclinic.service.ClinicSettingService;
import com.dentalclinic.service.TreatmentTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClinicSettingServiceTest {

    @Mock
    private ClinicSettingRepository settingRepository;

    @Mock
    private TreatmentTypeRepository treatmentTypeRepository;

    @InjectMocks
    private ClinicSettingService clinicSettingService;

    @InjectMocks
    private TreatmentTypeService treatmentTypeService;

    @Test
    @DisplayName("Unit Test: getClinicCharge Returns Configured Value from DB")
    public void testGetClinicCharge() {
        ClinicSetting setting = new ClinicSetting("clinic_charge", "600.00", "Static Clinic Facility Charge");
        when(settingRepository.findBySettingKey("clinic_charge")).thenReturn(Optional.of(setting));

        BigDecimal charge = clinicSettingService.getClinicCharge();
        assertEquals(new BigDecimal("600.00"), charge);
    }

    @Test
    @DisplayName("Unit Test: saveTreatmentType Saves Tariff Procedure and Price")
    public void testSaveTreatmentType() {
        TreatmentType treatment = new TreatmentType("Composite Filling", "Restorative filling", new BigDecimal("3000.00"));
        treatment.setTreatmentTypeId(1);

        when(treatmentTypeRepository.existsByTreatmentNameIgnoreCaseAndIsActiveTrue("Composite Filling")).thenReturn(false);
        when(treatmentTypeRepository.save(any(TreatmentType.class))).thenReturn(treatment);

        TreatmentTypeDTO inputDTO = new TreatmentTypeDTO(null, "Composite Filling", "Restorative filling", new BigDecimal("3000.00"), true);
        TreatmentTypeDTO result = treatmentTypeService.saveTreatmentType(inputDTO);

        assertNotNull(result);
        assertEquals(1, result.getTreatmentTypeId());
        assertEquals("Composite Filling", result.getTreatmentName());
        assertEquals(new BigDecimal("3000.00"), result.getBaseCost());
    }
}
