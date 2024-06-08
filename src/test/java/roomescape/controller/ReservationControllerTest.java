package roomescape.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.controller.dto.UserReservationSaveRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.*;
import roomescape.service.dto.response.*;
import roomescape.service.reservation.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ReservationController.class)
public class ReservationControllerTest extends RestDocsTestSupport {

    @MockBean
    private ReservationService reservationService;

    @Test
    @DisplayName("유저의 예약 생성")
    public void saveReservation_201() throws Exception {
        //given
        UserReservationSaveRequest request = new UserReservationSaveRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L,
                "testPaymentKey",
                "testOrderId",
                "testAmount",
                "testPaymentType"
        );

        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "user", "USER"),
                LocalDate.now().plusDays(1),
                new ReservationTimeResponse(1L, LocalTime.of(9, 0, 0)),
                new ThemeResponse(1L, "테마1", "테마 설명", "섬네일 링크")
        );

        Mockito.when(reservationService.saveUserReservation(any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.member.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.time.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.theme.id").value(1L))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("오늘 이후의 날짜만 가능합니다.")),
                                fieldWithPath("timeId")
                                        .type(NUMBER)
                                        .description("예약 시간 아이디")
                                        .attributes(constraints("예약날짜가 오늘이라면 현재 이후의 시간 아이디만 가능합니다.")),
                                fieldWithPath("themeId")
                                        .type(NUMBER)
                                        .description("테마 아이디"),
                                fieldWithPath("paymentKey")
                                        .type(STRING)
                                        .description("결제 키")
                                        .attributes(constraints("토스 페이먼트에서 발급하는 결제 키입니다.")),
                                fieldWithPath("orderId")
                                        .type(STRING)
                                        .description("주문 아이디"),
                                fieldWithPath("amount")
                                        .type(STRING)
                                        .description("결제 금액")
                                        .attributes(constraints("방탈출 예약 비용입니다.")),
                                fieldWithPath("paymentType")
                                        .type(STRING)
                                        .description("결제 유형")
                                        .attributes(constraints("NORMAL : 일반 결제 / BRANDPAY : 브랜드 페이 / KEYIN : 키인 결제"))
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("예약 아이디")
                                        .attributes(constraints("양수의 예약 아이디입니다")),
                                fieldWithPath("member")
                                        .type(MemberResponse.class)
                                        .description("예약한 회원 정보"),
                                fieldWithPath("member.id")
                                        .type(NUMBER)
                                        .description("회원 id"),
                                fieldWithPath("member.name")
                                        .type(STRING)
                                        .description("회원 이름"),
                                fieldWithPath("member.role")
                                        .type(STRING)
                                        .description("회원 권한")
                                        .attributes(constraints("ADMIN: 어드민, USER : 사용자")),
                                fieldWithPath("date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("예약된 날짜(오늘 이후만 가능)")),
                                fieldWithPath("time")
                                        .type(ReservationTimeResponse.class)
                                        .description("예약된 시간 정보"),
                                fieldWithPath("time.id")
                                        .type(NUMBER)
                                        .description("예약 시간 id"),
                                fieldWithPath("time.startAt")
                                        .type(LocalTime.class)
                                        .description("예약 시간"),
                                fieldWithPath("theme")
                                        .type(ThemeResponse.class)
                                        .description("예약된 테마 정보"),
                                fieldWithPath("theme.id")
                                        .type(NUMBER)
                                        .description("테마 id"),
                                fieldWithPath("theme.name")
                                        .type(STRING)
                                        .description("테마 이름"),
                                fieldWithPath("theme.description")
                                        .type(STRING)
                                        .description("테마 설명"),
                                fieldWithPath("theme.thumbnail")
                                        .type(STRING)
                                        .description("테마 썸네일 링크")
                        )
                ));
    }

    @Test
    @DisplayName("나의 예약 조회")
    public void getMyReservations_200() throws Exception {
        //given
        Theme theme = new Theme("테마", "테마_설명", "테마_섬네일링크");
        ReservationTime time = new ReservationTime(LocalTime.of(9, 0, 0));
        Member member = new Member("user", "email@email.com", "password", Role.USER);
        
        ReservationSlot slot = new ReservationSlot(LocalDate.now().plusDays(1), time, theme);
        ReservationSlot slot2 = new ReservationSlot(LocalDate.now().plusDays(2), time, theme);
        
        Reservation reservation1 = new Reservation(member, slot);

        List<UserReservationResponse> response = List.of(
                        new UserReservationResponse(
                                1L,
                                slot,
                                ReservationStatus.RESERVED,
                                OptionalLong.empty(),
                                Optional.of(new Payment("testPaymentKey", "1000", reservation1))
                        ),
                        new UserReservationResponse(
                                2L,
                                slot2,
                                ReservationStatus.WAITING,
                                OptionalLong.of(2L),
                                Optional.empty()
                        )
                );

        Mockito.when(reservationService.findAllUserReservation(any()))
                .thenReturn(response);
        
        mockMvc.perform(get("/reservations-mine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", USER_TOKEN))
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("reservationViewResponses")
                                        .type(ARRAY)
                                        .description("나의 예약 목록"),
                                fieldWithPath("reservationViewResponses[].id")
                                        .type(NUMBER)
                                        .description("나의 예약 아이디")
                                        .attributes(constraints("positive")),
                                fieldWithPath("reservationViewResponses[].theme")
                                        .type(STRING)
                                        .description("테마 이름"),
                                fieldWithPath("reservationViewResponses[].date")
                                        .type(LocalDate.class)
                                        .description("예약 날짜")
                                        .attributes(constraints("오늘 이후의 날짜입니다.")),
                                fieldWithPath("reservationViewResponses[].time")
                                        .type(LocalTime.class)
                                        .description("예약 시간"),
                                fieldWithPath("reservationViewResponses[].status")
                                        .type(STRING)
                                        .description("예약 상태")
                                        .attributes(constraints("예약 / 0번째 예약 대기")),
                                fieldWithPath("reservationViewResponses[].paymentKey")
                                        .type(STRING)
                                        .description("결제 키")
                                        .optional()
                                        .attributes(constraints("토스 페이먼츠에서 발급된 결제 키입니다.")),
                                fieldWithPath("reservationViewResponses[].amount")
                                        .type(STRING)
                                        .description("결제 금액")
                                        .optional()
                                        .attributes(constraints("positive"))
                        )
                )
        );
    }
}
