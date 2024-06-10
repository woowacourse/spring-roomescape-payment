package roomescape.document;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.controller.ReservationController;
import roomescape.document.config.RestDocsSupport;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.fixture.ReservationFixture;
import roomescape.service.ReservationService;

@WebMvcTest(ReservationController.class)
public class ReservationRestDocsTest extends RestDocsSupport {

    @MockBean
    private ReservationService reservationService;

    @Test
    public void saveReservation() throws Exception {
        ReservationRequest request = new ReservationRequest(
                LocalDate.now().plusDays(1), 1L, 1L, 1L);
        ReservationResponse response = ReservationResponse.from(ReservationFixture.DEFAULT_RESERVATION);
        given(reservationService.save(request))
                .willReturn(response);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("memberId", "1")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("memberId").description("예약 회원 id"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("themeId").description("예약 테마 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("테마 id"),
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time").description("예약 시간 정보"),
                                fieldWithPath("time.id").description("예약 시간 id"),
                                fieldWithPath("time.startAt").description("예약 시간"),
                                fieldWithPath("theme").description("예약 테마 정보"),
                                fieldWithPath("theme.id").description("예약 테마 id"),
                                fieldWithPath("theme.name").description("예약 테마 이름"),
                                fieldWithPath("theme.description").description("예약 테마 설명"),
                                fieldWithPath("theme.thumbnail").description("예약 테마 썸네일 URL"),
                                fieldWithPath("status").description("예약 상태")    // TODO: enum 문서화 방법 찾아보기
                        )
                ));
    }
}
