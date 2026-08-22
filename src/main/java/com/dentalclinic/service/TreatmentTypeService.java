package com.dentalclinic.service;

import com.dentalclinic.dto.TreatmentTypeDTO;
import com.dentalclinic.entity.TreatmentType;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.repository.TreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing Treatment procedure tariff catalog and procedure prices.
 * 
 * Layer: Business Logic Layer
 */
@Service
public class TreatmentTypeService {

    private final TreatmentTypeRepository treatmentTypeRepository;

    @Autowired
    public TreatmentTypeService(TreatmentTypeRepository treatmentTypeRepository) {
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    public List<TreatmentTypeDTO> getAllActiveTreatmentTypes() {
        return treatmentTypeRepository.findByIsActiveTrue()
                .stream()
                .map(t -> new TreatmentTypeDTO(t.getTreatmentTypeId(), t.getTreatmentName(), t.getDescription(), t.getBaseCost(), t.getIsActive()))
                .collect(Collectors.toList());
    }

    public List<TreatmentTypeDTO> getAllTreatmentTypes() {
        return treatmentTypeRepository.findAll()
                .stream()
                .map(t -> new TreatmentTypeDTO(t.getTreatmentTypeId(), t.getTreatmentName(), t.getDescription(), t.getBaseCost(), t.getIsActive()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TreatmentTypeDTO saveTreatmentType(TreatmentTypeDTO dto) {
        if (dto.getBaseCost() != null && dto.getBaseCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Treatment base price cannot be negative.");
        }

        TreatmentType treatment;
        if (dto.getTreatmentTypeId() != null) {
            treatment = treatmentTypeRepository.findById(dto.getTreatmentTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Treatment procedure not found with ID: " + dto.getTreatmentTypeId()));
            treatment.setTreatmentName(dto.getTreatmentName());
            treatment.setDescription(dto.getDescription());
            treatment.setBaseCost(dto.getBaseCost());
            if (dto.getIsActive() != null) {
                treatment.setIsActive(dto.getIsActive());
            }
        } else {
            if (treatmentTypeRepository.existsByTreatmentNameIgnoreCaseAndIsActiveTrue(dto.getTreatmentName())) {
                throw new IllegalArgumentException("A treatment procedure named '" + dto.getTreatmentName() + "' already exists.");
            }
            treatment = new TreatmentType(dto.getTreatmentName(), dto.getDescription(), dto.getBaseCost());
        }

        TreatmentType saved = treatmentTypeRepository.save(treatment);
        return new TreatmentTypeDTO(saved.getTreatmentTypeId(), saved.getTreatmentName(), saved.getDescription(), saved.getBaseCost(), saved.getIsActive());
    }
}
