package com.dentalclinic;

import com.dentalclinic.dto.TreatmentTypeDTO;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.repository.TreatmentTypeRepository;
import com.dentalclinic.service.TreatmentTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TreatmentTypeServiceTest {

    @Mock
    private TreatmentTypeRepository treatmentTypeRepository;

    @InjectMocks
    private TreatmentTypeService treatmentTypeService;

    private TreatmentType sampleTreatment;

    @BeforeEach
    public void setUp() {
        sampleTreatment = new TreatmentType("Dental Scaling & Polishing", "Deep cleaning of teeth", new BigDecimal("3500.00"));
        sampleTreatment.setTreatmentTypeId(1);
        sampleTreatment.setIsActive(true);
    }

    @Test
    @DisplayName("Scenario 1: Retrieve All Active Treatment Types")
    public void testGetAllActiveTreatmentTypes() {
        when(treatmentTypeRepository.findByIsActiveTrue()).thenReturn(List.of(sampleTreatment));

        List<TreatmentTypeDTO> result = treatmentTypeService.getAllActiveTreatmentTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dental Scaling & Polishing", result.get(0).getTreatmentName());
        assertEquals(new BigDecimal("3500.00"), result.get(0).getBaseCost());
    }

    @Test
    @DisplayName("Scenario 2: Save New Valid Treatment Procedure")
    public void testSaveTreatmentType_Success() {
        TreatmentTypeDTO dto = new TreatmentTypeDTO(null, "Root Canal Therapy", "Multi-visit endodontic treatment", new BigDecimal("12000.00"), true);
        when(treatmentTypeRepository.save(any(TreatmentType.class))).thenReturn(sampleTreatment);

        TreatmentTypeDTO result = treatmentTypeService.saveTreatmentType(dto);

        assertNotNull(result);
        verify(treatmentTypeRepository, times(1)).save(any(TreatmentType.class));
    }

    @Test
    @DisplayName("Scenario 3: Save Treatment with Negative Base Cost Throws IllegalArgumentException")
    public void testSaveTreatmentType_NegativeCost_ThrowsException() {
        TreatmentTypeDTO dto = new TreatmentTypeDTO(null, "Invalid Procedure", "Negative cost", new BigDecimal("-500.00"), true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> treatmentTypeService.saveTreatmentType(dto)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("negative") || ex.getMessage().toLowerCase().contains("cannot"));
        verify(treatmentTypeRepository, never()).save(any(TreatmentType.class));
    }

    @Test
    @DisplayName("Scenario 4: Retrieve All Treatment Types Catalog")
    public void testGetAllTreatmentTypes() {
        when(treatmentTypeRepository.findAll()).thenReturn(List.of(sampleTreatment));

        List<TreatmentTypeDTO> result = treatmentTypeService.getAllTreatmentTypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dental Scaling & Polishing", result.get(0).getTreatmentName());
    }
}
