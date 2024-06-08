package roomescape.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.request.WaitingRequest;
import roomescape.service.dto.response.WaitingResponse;
import roomescape.service.reservation.WaitingService;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WaitingController.class)
class WaitingControllerTest extends RestDocsTestSupport {

    @MockBean
    private WaitingService waitingService;

    @Test
    @DisplayName("예약 대기 저장")
    void saveWaiting() throws Exception {
        //given
        WaitingRequest request = new WaitingRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L
        );

        WaitingResponse response = new WaitingResponse(
                1L,
                "userName",
                "themeName",
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0, 0)
        );

        Mockito.when(waitingService.saveWaiting(any(), anyLong()))
                .thenReturn(response);

        mockMvc.perform(post("/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr("loginMember", USER)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.theme").value(response.theme()))
                .andExpect(jsonPath("$.date").value(response.date().toString()))
                .andExpect(jsonPath("$.time").value(response.time().toString()))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date")
                                        .type(LocalDate.class)
                                        .description("예약 대기 날짜")
                                        .attributes(constraints("오늘 이후의 날짜만 가능합니다.")),
                                fieldWithPath("timeId")
                                        .type(NUMBER)
                                        .description("시간 아이디")
                                        .attributes(constraints("예약 대기 날짜가 오늘이라면 현재 이후의 시간 아이디만 가능합니다.")),
                                fieldWithPath("themeId")
                                        .type(NUMBER)
                                        .description("테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("예약 아이디")
                                        .attributes(constraints("양수의 예약 아이디입니다")),
                                fieldWithPath("name")
                                        .type(STRING)
                                        .description("예약 회원 이름"),
                                fieldWithPath("date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("예약된 날짜(오늘 이후만 가능)")),
                                fieldWithPath("time")
                                        .type(LocalTime.class)
                                        .description("예약된 시간 정보"),
                                fieldWithPath("theme")
                                        .type(STRING)
                                        .description("예약된 테마 이름")
                        )
                ));
    }

    @Test
    @DisplayName("예약 삭제")
    void deleteWaiting() throws Exception {
        //given
        mockMvc.perform(delete("/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                        .requestAttr("loginMember", USER)
                        .param("id", "1")
                )
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print())
                .andDo(restDocs.document());
    }
}