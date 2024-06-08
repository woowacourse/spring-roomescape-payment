package roomescape.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.response.ReservationTimeResponse;
import roomescape.service.dto.response.ReservationTimeResponses;
import roomescape.service.reservation.ReservationTimeService;

import java.time.LocalTime;
import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationTimeController.class)
class ReservationTimeControllerTest extends RestDocsTestSupport {

    @MockBean
    private ReservationTimeService timeService;

    @Test
    void getTimes() throws Exception {
        ReservationTimeResponses response = new ReservationTimeResponses(
                List.of(
                        new ReservationTimeResponse(1L, LocalTime.of(9, 0, 0)),
                        new ReservationTimeResponse(2L, LocalTime.of(10, 0, 0))
                )
        );

        Mockito.when(timeService.getTimes()).thenReturn(response);

        mockMvc.perform(get("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                        .requestAttr("loginMember", USER)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("reservationTimeResponses")
                                                .type(JsonFieldType.ARRAY)
                                                .description("전체 시간 목록"),
                                        fieldWithPath("reservationTimeResponses[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시간 아이디")
                                                .attributes(constraints("positive")),
                                        fieldWithPath("reservationTimeResponses[].startAt")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 시간")
                                )
                        )
                );
    }

    @Test
    void getTimesWithBooked() {
        // TODO
    }
}