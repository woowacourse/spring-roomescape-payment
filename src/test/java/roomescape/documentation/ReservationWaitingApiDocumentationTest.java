package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import roomescape.controller.api.ReservationWaitingController;
import roomescape.controller.dto.request.WaitingRequest;
import roomescape.service.ReservationWaitingService;
import roomescape.service.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationWaitingApiDocumentationTest extends BaseDocumentationTest {

    private final ReservationWaitingService reservationWaitingService = Mockito.mock(ReservationWaitingService.class);

    @Test
    @DisplayName("예약 대기를 생성한다.")
    void createReservationWaiting() throws Exception {
        ReservationResponse response = new ReservationResponse(1L, LocalDate.parse("2024-06-10"), "프린", LocalTime.parse("10:00"), "테마명1");
        when(reservationWaitingService.addReservationWaiting(any()))
                .thenReturn(response);
        WaitingRequest request = new WaitingRequest(LocalDate.parse("2024-06-10"), 1L, 1L);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(memberCookie)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(document("reservationWaiting/create",
                        requestFields(
                                fieldWithPath("date").description("예약 대기 날짜"),
                                fieldWithPath("timeId").description("예약 대기 시간 id"),
                                fieldWithPath("themeId").description("테마 id")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("생성된 예약 대기의 URI")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 대기 id"),
                                fieldWithPath("date").description("예약 대기 날짜"),
                                fieldWithPath("name").description("예약 대기자명"),
                                fieldWithPath("startAt").description("예약 대기 시간"),
                                fieldWithPath("theme").description("테마명")
                        )
                ));
    }

    @Test
    @DisplayName("예약 대기를 삭제한다.")
    void deleteReservationWaiting() throws Exception {
        doNothing().when(reservationWaitingService)
                .deleteReservationWaiting(anyLong(), any());

        mockMvc.perform(delete("/waitings/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(memberCookie)
                )
                .andExpect(status().isNoContent())
                .andDo(document("reservationWaiting/delete",
                        pathParameters(
                                parameterWithName("id").description("예약 대기 id")
                        )
                ));
    }

    @Override
    Object controller() {
        return new ReservationWaitingController(reservationWaitingService);
    }
}
