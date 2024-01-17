package ru.veselov.companybot.service.impl;

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
import ru.veselov.companybot.dto.DivisionCreateDTO;
import ru.veselov.companybot.entity.DivisionEntity;
import ru.veselov.companybot.mapper.DivisionMapperImpl;
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
        DivisionCreateDTO divisionDTO = TestUtils.getDivisionDTO();
        Mockito.when(divisionRepository.findByName(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(divisionRepository.save(Mockito.any())).thenReturn(new DivisionEntity());
        divisionService.save(divisionDTO);

        Mockito.verify(divisionRepository, Mockito.times(1)).save(entityArgumentCaptor.capture());
        DivisionEntity captured = entityArgumentCaptor.getValue();
        Assertions.assertThat(captured.getName()).isEqualTo(divisionDTO.getName());
        Assertions.assertThat(captured.getDescription()).isEqualTo(divisionDTO.getDescription());
    }

}
