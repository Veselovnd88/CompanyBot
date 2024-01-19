package ru.veselov.companybot.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.companybot.dto.DivisionDTO;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.exception.DivisionAlreadyExistsException;
import ru.veselov.companybot.exception.DivisionNotFoundException;
import ru.veselov.companybot.exception.ObjectAlreadyExistsException;
import ru.veselov.companybot.mapper.DivisionMapperImpl;
import ru.veselov.companybot.model.DivisionModel;
import ru.veselov.companybot.repository.DivisionRepository;
import ru.veselov.companybot.util.TestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DivisionServiceImplTest {

    @Mock
    DivisionRepository divisionRepository;

    @InjectMocks
    DivisionServiceImpl divisionService;

    @Captor
    ArgumentCaptor<DivisionEntity> entityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(divisionService, "divisionMapper", new DivisionMapperImpl());
    }

    @Test
    void save_AllOk_SaveAndReturnModel() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        Mockito.when(divisionRepository.findByName(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(divisionRepository.save(Mockito.any())).thenReturn(new DivisionEntity());
        divisionService.save(divisionDTO);

        Mockito.verify(divisionRepository, Mockito.times(1)).save(entityArgumentCaptor.capture());
        DivisionEntity captured = entityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getName()).isEqualTo(divisionDTO.getName());
        Assertions.assertThat(captured.getDescription()).isEqualTo(divisionDTO.getDescription());
    }

    @Test
    void save_NameExists_ThrowsException() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        Mockito.when(divisionRepository.findByName(Mockito.any())).thenReturn(Optional.of(new DivisionEntity()));

        Assertions.assertThatExceptionOfType(DivisionAlreadyExistsException.class)
                .isThrownBy(() -> divisionService.save(divisionDTO))
                .isInstanceOf(ObjectAlreadyExistsException.class);
    }

    @Test
    void findById_NoDivision_ThrowsException() {
        Mockito.when(divisionRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(DivisionNotFoundException.class)
                .isThrownBy(() -> divisionService.findById(TestUtils.DIVISION_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findById_AllOk_ReturnDivision() {
        Mockito.when(divisionRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(DivisionEntity.builder().divisionId(TestUtils.DIVISION_ID)
                        .name(TestUtils.DIVISION_NAME).description(TestUtils.DIVISION_DESC)
                        .build()));

        DivisionModel divisionModel = divisionService.findById(Mockito.any());

        Assertions.assertThat(divisionModel.getDescription()).isEqualTo(TestUtils.DIVISION_DESC);
        Assertions.assertThat(divisionModel.getName()).isEqualTo(TestUtils.DIVISION_NAME);
    }

    @Test
    void update_AllOk_UpdateAndReturnDivision() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        String newName = TestUtils.faker.elderScrolls().creature();
        divisionDTO.setName(newName);
        DivisionEntity divisionEntity = TestUtils.getDivisionEntity();
        Mockito.when(divisionRepository.findById(TestUtils.DIVISION_ID))
                .thenReturn(Optional.of(TestUtils.getDivisionEntity()));
        Mockito.when(divisionRepository.findByName(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(divisionRepository.save(Mockito.any())).thenReturn(divisionEntity);

        divisionService.update(TestUtils.DIVISION_ID, divisionDTO);

        Mockito.verify(divisionRepository, Mockito.times(1)).save(entityArgumentCaptor.capture());
        DivisionEntity captured = entityArgumentCaptor.getValue();
        Assertions.assertThat(divisionDTO.getName()).isNotEqualTo(divisionEntity.getName());
        Assertions.assertThat(captured.getName()).isEqualTo(divisionDTO.getName());
        Assertions.assertThat(captured.getDescription()).isEqualTo(divisionDTO.getDescription());
    }

    @Test
    void update_DivisionForUpdateNotFound_ThrowException() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        Mockito.when(divisionRepository.findById(TestUtils.DIVISION_ID))
                .thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(DivisionNotFoundException.class)
                .isThrownBy(() -> divisionService.update(TestUtils.DIVISION_ID, divisionDTO))
                .isInstanceOf(EntityNotFoundException.class);

        Mockito.verify(divisionRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void update_NameAlreadyExists_ThrowException() {
        DivisionDTO divisionDTO = TestUtils.getDivisionDTO();
        divisionDTO.setName("Huggy Wuggy");
        Mockito.when(divisionRepository.findById(TestUtils.DIVISION_ID))
                .thenReturn(Optional.of(TestUtils.getDivisionEntity()));
        Mockito.when(divisionRepository.findByName(Mockito.any())) //FIXME exists by name
                .thenReturn(Optional.of(TestUtils.getDivisionEntity()));

        Assertions.assertThatExceptionOfType(DivisionAlreadyExistsException.class)
                .isThrownBy(() -> divisionService.update(TestUtils.DIVISION_ID, divisionDTO))
                .isInstanceOf(ObjectAlreadyExistsException.class);

        Mockito.verify(divisionRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void delete_AllOk_DeleteAndReturnVoid() {
        Assertions.assertThatNoException().isThrownBy(() -> divisionService.delete(TestUtils.DIVISION_ID));
    }

}
