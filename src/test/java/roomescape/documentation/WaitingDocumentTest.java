package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.WaitingService;
import roomescape.application.dto.response.WaitingResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.Theme;
import roomescape.presentation.api.WaitingController;
import roomescape.presentation.dto.request.WaitingWebRequest;

@WebMvcTest(WaitingController.class)
class WaitingDocumentTest extends AbstractDocumentTest {

    private static final Waiting WAITING = new Waiting(
            1L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 8),
                    new ReservationTime(1L, LocalTime.of(10, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    @MockBean
    private WaitingService waitingService;

    @Test
    @DisplayName("예약 대기를 추가한다.")
    void addWaiting() throws Exception {
        WaitingWebRequest request = new WaitingWebRequest(
                LocalDate.of(2024, 5, 8),
                1L,
                1L
        );
        WaitingResponse response = WaitingResponse.from(WAITING);

        when(waitingService.addWaiting(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .cookie(new Cookie("token", "{MEMBER_TOKEN}"))
        ).andExpectAll(
                status().isCreated(),
                content().json(objectMapper.writeValueAsString(response))
        ).andDo(
                document("waiting/add",
                        responseFields(
                                fieldWithPath("id").description("예약 대기 식별자"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 식별자"),
                                fieldWithPath("time.startAt").description("시작 시간"),
                                fieldWithPath("theme.id").description("테마 식별자"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 이미지 URL"),
                                fieldWithPath("member.id").description("회원 식별자"),
                                fieldWithPath("member.email").description("회원 이메일"),
                                fieldWithPath("member.name").description("회원 이름"),
                                fieldWithPath("member.role").description("회원 권한")
                        ))
        );
    }

    @Test
    @DisplayName("예약 대기를 취소한다.")
    void deleteWaiting() throws Exception {
        Long id = 1L;

        doNothing()
                .when(waitingService).deleteWaitingById(anyLong(), anyLong());

        mockMvc.perform(
                delete("/waitings/{id}", id)
                        .cookie(new Cookie("token", "{MEMBER_TOKEN}"))
        ).andExpectAll(
                status().isNoContent()
        ).andDo(
                document("waiting/delete")
        );
    }
}
