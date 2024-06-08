package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.ReservationService;
import roomescape.application.ReservationWaitingService;
import roomescape.application.dto.response.MyReservationResponse;
import roomescape.application.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.dto.ReservationWithPaymentDto;
import roomescape.domain.reservation.dto.WaitingWithRankDto;
import roomescape.presentation.api.ReservationController;
import roomescape.presentation.dto.Accessor;
import roomescape.presentation.dto.request.ReservationWebRequest;

@WebMvcTest(ReservationController.class)
class ReservationDocumentTest extends AbstractDocumentTest {

    private static final Reservation RESERVATION = new Reservation(
            1L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 8),
                    new ReservationTime(1L, LocalTime.of(10, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    private static final Waiting WAITING = new Waiting(
            1L,
            new ReservationDetail(
                    LocalDate.of(2024, 5, 8),
                    new ReservationTime(2L, LocalTime.of(11, 0)),
                    new Theme(1L, "테마", "테마 설명", "https://image.com")
            ),
            new Member(1L, "user@gmail.clom", "password", "유저", Role.USER)
    );

    private static final Payment PAYMENT = new Payment(
            1L,
            "WTESTMC4xOTI0NjcwNjE1MzNw",
            "tgen_20240604162216qwe78",
            BigDecimal.valueOf(1000),
            RESERVATION
    );

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private ReservationWaitingService reservationWaitingService;

    @Test
    @DisplayName("나의 예약과 예약 대기들을 대기 순번과 함께 조회한다.")
    void getMyReservationWithRanks() throws Exception {
        List<MyReservationResponse> responses = List.of(
                MyReservationResponse.from(new ReservationWithPaymentDto(RESERVATION, PAYMENT)),
                MyReservationResponse.from(new WaitingWithRankDto(WAITING, 1L))
        );

        when(authArgumentResolver.supportsParameter(any()))
                .thenReturn(true);
        when(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(new Accessor(1L, "user@gmail.com", "유저", Role.USER));
        when(reservationWaitingService.getMyReservationAndWaitingWithRanks(anyLong()))
                .thenReturn(responses);

        mockMvc.perform(
                        get("/reservations/mine")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(new Cookie("token", "{MEMBER_TOKEN}"))
                ).andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(responses))
                )
                .andDo(document("reservations/list/mine",
                                responseFields(
                                        fieldWithPath("[].id").description("예약 식별자"),
                                        fieldWithPath("[].date").description("예약 날짜"),
                                        fieldWithPath("[].time").description("예약 시간"),
                                        fieldWithPath("[].theme").description("테마 이름"),
                                        fieldWithPath("[].status").description("예약 상태"),
                                        fieldWithPath("[].rank").description("대기 순번"),
                                        subsectionWithPath("[].payment").description("결제 정보 (없을 수 있음)").optional()
                                )
                        )
                );
    }

    @Test
    @DisplayName("예약을 생성한다.")
    void addReservation() throws Exception {
        ReservationWebRequest request = new ReservationWebRequest(
                LocalDate.of(2024, 5, 8),
                1L,
                1L,
                "tgen_20240604162216qwe78",
                "WTESTMC4xOT",
                BigDecimal.valueOf(1000)
        );
        ReservationResponse response = ReservationResponse.from(RESERVATION);

        when(reservationService.addReservation(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .cookie(new Cookie("token", "{MEMBER_TOKEN}"))
        ).andExpectAll(
                status().isCreated(),
                content().json(objectMapper.writeValueAsString(response))
        ).andDo(
                document("reservations/add",
                        responseFields(
                                fieldWithPath("id").description("예약 식별자"),
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
}
