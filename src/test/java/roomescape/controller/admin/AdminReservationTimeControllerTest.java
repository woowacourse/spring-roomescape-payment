package roomescape.controller.admin;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.request.ReservationTimeSaveRequest;
import roomescape.service.dto.response.ReservationTimeResponse;
import roomescape.service.dto.response.ReservationTimeResponses;
import roomescape.service.reservation.ReservationTimeService;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminReservationTimeController.class)
class AdminReservationTimeControllerTest extends RestDocsTestSupport {

    @MockBean
    private ReservationTimeService timeService;

    @Test
    void getTimes() throws Exception {
        ReservationTimeResponses response = new ReservationTimeResponses(
                List.of(
                        new ReservationTimeResponse(1L, LocalTime.of(9,0,0)),
                        new ReservationTimeResponse(2L, LocalTime.of(10,0,0))
                )
        );

        Mockito.when(timeService.getTimes()).thenReturn(response);

        mockMvc.perform(get("/admin/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
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
                                                .attributes(constraints( "positive")),
                                        fieldWithPath("reservationTimeResponses[].startAt")
                                                .type(JsonFieldType.STRING)
                                                .description("예약 시간")
                                )
                    )
                );
    }

    @Test
    @DisplayName("시간 저장")
    void saveTime() throws Exception {
        //given
        ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(LocalTime.of(9, 0, 0));
        ReservationTimeResponse response = new ReservationTimeResponse(1L, LocalTime.of(9,0,0));

        Mockito.when(timeService.saveTime(any())).thenReturn(response);

        mockMvc.perform(post("/admin/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.startAt").value(response.startAt().toString()))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("startAt")
                                        .type(LocalTime.class)
                                        .description("예약 시간")
                                        .attributes(constraints( "not null"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("시간 아이디")
                                        .attributes(constraints( "양수인 시간 아이디입니다.")),
                                fieldWithPath("startAt")
                                        .type(LocalTime.class)
                                        .description("예약 시간")
                                        .attributes(constraints( "not null"))
                        )
                ));
    }

    @Test
    @DisplayName("시간 삭제")
    void deleteTime() throws Exception {
        //given
        mockMvc.perform(delete("/admin/times/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document());
    }
}