package roomescape.controller.admin;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.response.WaitingResponse;
import roomescape.service.dto.response.WaitingResponses;
import roomescape.service.reservation.WaitingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminWaitingController.class)
class AdminWaitingControllerTest extends RestDocsTestSupport {

    @MockBean
    private WaitingService waitingService;

    @Test
    void allWaitings() throws Exception {
        WaitingResponses response = new WaitingResponses(
                List.of(
                        new WaitingResponse(1L, "memberName1", "themeName1", LocalDate.now().plusDays(1L), LocalTime.of(9, 0, 0)),
                        new WaitingResponse(2L, "memberName2", "themeName2", LocalDate.now().plusDays(2L), LocalTime.of(9, 0, 0))
                )
        );

        Mockito.when(waitingService.findAllWaitings()).thenReturn(response);

        mockMvc.perform(get("/admin/waitings/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("waitingResponses")
                                                .type(ARRAY)
                                                .description("전체 예약 대기 목록"),
                                        fieldWithPath("waitingResponses[].id")
                                                .type(NUMBER)
                                                .description("예약 대기 아이디")
                                                .attributes(constraints("positive")),
                                        fieldWithPath("waitingResponses[].name")
                                                .type(STRING)
                                                .description("대기 멤버 이름")
                                                .attributes(constraints("not null")),
                                        fieldWithPath("waitingResponses[].theme")
                                                .type(STRING)
                                                .description("대기 테마 이름"),
                                        fieldWithPath("waitingResponses[].date")
                                                .type(LocalDate.class)
                                                .description("예약 대기 날짜")
                                                .attributes(constraints("오늘 이후만 가능합니다.")),
                                        fieldWithPath("waitingResponses[].time")
                                                .type(LocalDate.class)
                                                .description("예약 대기 시간")
                                                .attributes(constraints("오늘이 예약일이라면 현재 이후의 시간만 가능합니다."))
                                )
                        )
                );
    }

    @Test
    void deleteWaiting() throws Exception {
        //given
        mockMvc.perform(delete("/admin/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                        .param("id", "1")
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document());
    }
}