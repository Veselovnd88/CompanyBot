package ru.veselov.companybot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.veselov.companybot.dto.DivisionDTO;

import java.util.UUID;

@UtilityClass
public class MockMvcUtils {

    public static MockHttpServletRequestBuilder createDivision(DivisionDTO divisionDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(RestUrl.DIVISIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.jsonStringFromObject(divisionDTO));
    }

    public static MockHttpServletRequestBuilder updateDivision(DivisionDTO divisionDTO, UUID divisionId) throws JsonProcessingException {
        return MockMvcRequestBuilders.put(RestUrl.DIVISIONS + "/{divisionId}", divisionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.jsonStringFromObject(divisionDTO));
    }

    public static MockHttpServletRequestBuilder deleteDivision(UUID divisionId) {
        return MockMvcRequestBuilders.delete(RestUrl.DIVISIONS + "/{divisionId}", divisionId);
    }

    public static MockHttpServletRequestBuilder getDivision(UUID divisionId) {
        return MockMvcRequestBuilders.get(RestUrl.DIVISIONS + "/{divisionId}", divisionId);
    }

    public static MockHttpServletRequestBuilder getDivisions() {
        return MockMvcRequestBuilders.get(RestUrl.DIVISIONS);
    }

    public static MockHttpServletRequestBuilder getInquiries() {
        return MockMvcRequestBuilders.get(RestUrl.INQUIRIES);
    }

}
