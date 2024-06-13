package roomescape.documentation.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.ReservationService;
import roomescape.application.dto.response.ReservationResponse;
import roomescape.documentation.AbstractDocumentTest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.Theme;
import roomescape.presentation.api.admin.AdminReservationController;
import roomescape.presentation.dto.request.AdminReservationWebRequest;

@WebMvcTest(AdminReservationController.class)
class AdminReservationDocumentTest extends AbstractDocumentTest {

    private static final Reservation RESERVATION_1 = new Reservation(
            1L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 8),
                    new ReservationTime(1L, LocalTime.of(10, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    private static final Reservation RESERVATION_2 = new Reservation(
            2L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 9),
                    new ReservationTime(2L, LocalTime.of(11, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("조건에 맞는 예약 목록을 조회한다.")
    void getReservationsByConditions() throws Exception {
        List<ReservationResponse> responses = List.of(
                ReservationResponse.from(RESERVATION_1),
                ReservationResponse.from(RESERVATION_2)
        );

        LocalDate from = LocalDate.of(2024, 5, 8);
        LocalDate to = LocalDate.of(2024, 5, 9);
        when(reservationService.getReservationsByConditions(1L, 1L, from, to))
                .thenReturn(responses);

        mockMvc.perform(
                get("/admin/reservations")
                        .param("memberId", "1")
                        .param("themeId", "1")
                        .param("dateFrom", from.toString())
                        .param("dateTo", to.toString())
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                document("admin/reservations/list",
                        queryParameters(
                                parameterWithName("memberId").description("회원 식별자"),
                                parameterWithName("themeId").description("회원 식별자"),
                                parameterWithName("dateFrom").description("조회 시작 날짜"),
                                parameterWithName("dateTo").description("조회 종료 날짜")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("예약 식별자"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].time.id").description("예약 시간 식별자"),
                                fieldWithPath("[].time.startAt").description("예약 시간"),
                                fieldWithPath("[].theme.id").description("테마 식별자"),
                                fieldWithPath("[].theme.name").description("테마 이름"),
                                fieldWithPath("[].theme.description").description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").description("테마 이미지 경로"),
                                fieldWithPath("[].member.id").description("회원 식별자"),
                                fieldWithPath("[].member.email").description("회원 이메일"),
                                fieldWithPath("[].member.name").description("회원 이름"),
                                fieldWithPath("[].member.role").description("회원 권한")
                        )));
    }

    @Test
    @DisplayName("예약을 생성한다.")
    void addReservation() throws Exception {
        ReservationResponse response = ReservationResponse.from(RESERVATION_1);
        AdminReservationWebRequest request = new AdminReservationWebRequest(
                LocalDate.of(2024, 5, 8),
                1L,
                1L,
                1L
        );

        when(reservationService.addReservation(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(
                document("admin/reservations/add",
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 식별자"),
                                fieldWithPath("themeId").description("테마 식별자"),
                                fieldWithPath("memberId").description("회원 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 식별자"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 식별자"),
                                fieldWithPath("time.startAt").description("예약 시간"),
                                fieldWithPath("theme.id").description("테마 식별자"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 이미지 경로"),
                                fieldWithPath("member.id").description("회원 식별자"),
                                fieldWithPath("member.email").description("회원 이메일"),
                                fieldWithPath("member.name").description("회원 이름"),
                                fieldWithPath("member.role").description("회원 권한")
                        )));
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void deleteReservation() throws Exception {
        Long id = 1L;
        doNothing()
                .when(reservationService).deleteReservationById(id);

        mockMvc.perform(
                delete("/admin/reservations/{id}", id)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isNoContent()
        ).andDo(
                document("admin/reservations/delete",
                        pathParameters(
                                parameterWithName("id").description("예약 식별자")
                        ))
        );
    }
}
