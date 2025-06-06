package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.ReservationTimeService;
import roomescape.presentation.api.ReservationTimeApiController;
import roomescape.presentation.dto.request.ReservationTimeRequest;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;
import roomescape.presentation.dto.response.TimeSlotResponse;

@WebMvcTest(value = {ReservationTimeApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
class ReservationTimeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JJWTJwtUtil jwtUtil;

    @MockitoBean
    private ReservationTimeService reservationTimeService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    @Test
    void 예약_시간_생성에_성공한다() throws Exception {
        // given
        ReservationTimeRequest request = new ReservationTimeRequest("09:00");
        TimeSlotResponse response = new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0));
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        given(reservationTimeService.addAndGet(request)).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(post("/times")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/times/timeSlotId1"))
                .andExpectAll(
                        jsonPath("$.id").value("timeSlotId1"),
                        jsonPath("$.startAt").value("09:00:00"))
                .andDo(document("create-time-slot",
                        requestFields(
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시간")
                        )
                ));
    }

    @Test
    void 예약시간_전체_조회에_성공한다() throws Exception {
        // given
        TimeSlotResponse response1 = new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0));
        List<TimeSlotResponse> response = List.of(response1);
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(reservationTimeService.getAll()).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/times")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("timeSlotId1"),
                        jsonPath("$[0].startAt").value("09:00:00"))
                .andDo(document("get-all-time-slot",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 시간 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시간")
                        )
                ));
    }

    @Test
    void 전체_예약시간과_예약_여부_조회에_성공한다() throws Exception {
        // given
        ReservationTimeResponseWithBooked response1 = new ReservationTimeResponseWithBooked("timeSlotId1",
                LocalTime.of(9, 0), true);
        List<ReservationTimeResponseWithBooked> response = List.of(response1);
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(reservationTimeService.getAllByDateAndThemeId(LocalDate.of(2025, 1, 1), "themeId1")).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/times/possible")
                .cookie(new Cookie("authToken", token.value()))
                .param("date", "2025-01-01")
                .param("themeId", "themeId1")
        );
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("timeSlotId1"),
                        jsonPath("$[0].startAt").value("09:00:00"),
                        jsonPath("$[0].alreadyBooked").value(true))
                .andDo(document("get-all-time-slot-with-booked",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 시간 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("[].alreadyBooked").type(JsonFieldType.BOOLEAN).description("예약 여부")
                        )
                ));
    }

    @Test
    void 예약_시간_삭제에_성공한다() throws Exception {
        // given
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        // when
        ResultActions result = mockMvc.perform(delete("/times/1")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isNoContent())
                .andDo(document("delete-time-slot"));
    }
}