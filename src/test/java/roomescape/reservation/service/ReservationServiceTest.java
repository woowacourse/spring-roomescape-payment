package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static roomescape.reservation.fixture.ReservationFixture.MEMBER_ID_1_RESERVATION;
import static roomescape.reservation.fixture.ReservationFixture.PAST_DATE_RESERVATION_REQUEST;
import static roomescape.reservation.fixture.ReservationFixture.RESERVATION_ADD_REQUEST_WITH_INVALID_PAYMENTS;
import static roomescape.reservation.fixture.ReservationFixture.RESERVATION_ADD_REQUEST_WITH_VALID_PAYMENTS;
import static roomescape.reservation.fixture.ReservationFixture.RESERVATION_REQUEST_1;
import static roomescape.reservation.fixture.ReservationFixture.SAVED_RESERVATION_1;
import static roomescape.reservation.fixture.ReservationFixture.SAVED_RESERVATION_2;
import static roomescape.theme.fixture.ThemeFixture.THEME_1;
import static roomescape.time.fixture.ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.global.exception.IllegalRequestException;
import roomescape.global.exception.InternalServerException;
import roomescape.member.fixture.MemberFixture;
import roomescape.member.service.MemberService;
import roomescape.payment.config.PaymentConfig;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.service.TossPaymentClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationWithInformation;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.theme.service.ThemeService;
import roomescape.time.service.ReservationTimeService;

@ExtendWith(MockitoExtension.class)
@RestClientTest(TossPaymentClient.class)
@Import(PaymentConfig.class)
class ReservationServiceTest {

    private ReservationService reservationService;

    @Mock
    private MemberService memberService;

    @Mock
    private ReservationTimeService reservationTimeService;

    @Mock
    private ThemeService themeService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Autowired
    private TossPaymentClient paymentClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setData() {
        this.reservationService = new ReservationService(memberService, reservationTimeService,
                themeService, reservationRepository, paymentRepository, paymentClient);
    }

    @DisplayName("전체 예약을 조회하고 응답 형태로 반환할 수 있다")
    @Test
    void should_return_response_when_requested_all() {
        when(reservationRepository.findAll()).thenReturn(List.of(MEMBER_ID_1_RESERVATION, SAVED_RESERVATION_2));

        assertThat(reservationService.findAllReservation())
                .contains(new ReservationResponse(MEMBER_ID_1_RESERVATION),
                        new ReservationResponse(SAVED_RESERVATION_2));
    }

    @DisplayName("특정 유저의 예약 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_return_response_when_my_reservations_requested_all() {
        when(reservationRepository.findByMemberIdWithInformation(1L))
                .thenReturn(List.of(new ReservationWithInformation(MEMBER_ID_1_RESERVATION, 0, "PAYMENT_KEY", 1000)));

        assertThat(reservationService.findMemberReservationWithInformation(1L))
                .containsExactly(new MemberReservationResponse(new ReservationWithInformation(MEMBER_ID_1_RESERVATION, 0, "PAYMENT_KEY", 1000)));
    }

    @DisplayName("예약을 추가하고 응답을 반환할 수 있다")
    @Test
    void should_save_reservation_when_requested() {
        when(memberService.findById(any(Long.class))).thenReturn(MemberFixture.MEMBER_ID_1);
        when(reservationTimeService.findById(1L)).thenReturn(RESERVATION_TIME_10_00_ID_1);
        when(themeService.findById(1L)).thenReturn(THEME_1);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(MEMBER_ID_1_RESERVATION);

        ReservationResponse savedReservation = reservationService.saveReservation(RESERVATION_REQUEST_1);

        assertThat(savedReservation).isEqualTo(new ReservationResponse(MEMBER_ID_1_RESERVATION));
    }

    @DisplayName("현재보다 이전날짜로 예약 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_request_with_past_date() {
        when(memberService.findById(any(Long.class))).thenReturn(MemberFixture.MEMBER_ID_1);
        when(reservationTimeService.findById(1L)).thenReturn(RESERVATION_TIME_10_00_ID_1);
        when(themeService.findById(1L)).thenReturn(THEME_1);

        assertThatThrownBy(() -> reservationService.saveReservation(PAST_DATE_RESERVATION_REQUEST))
                .isInstanceOf(IllegalRequestException.class);
    }

    @DisplayName("내 이름으로 진행되고 있는 예약이 이미 존재하는 경우 예약 대기를 할 수 없다")
    @Test
    void should_not_wait_when_my_reservation_is_exist() {
        when(reservationRepository.findByDateAndTimeAndTheme(any(LocalDate.class), any(Long.class),
                any(Long.class))).thenReturn(List.of(
                MEMBER_ID_1_RESERVATION));

        assertThatThrownBy(() -> reservationService.saveReservation(RESERVATION_REQUEST_1))
                .isInstanceOf(IllegalRequestException.class);
    }

    @DisplayName("결제가 4xx 에러로 인해 승인되지 않으면 멤버의 예약 저장에 실패한다")
    @Test
    void should_throw_exception_when_reservation_not_confirmed_payments_4xx_error() {
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"error\": \"Bad Request\" }")); // 여기에 원하는 JSON 본문을 추가


        assertThatThrownBy(
                () -> reservationService.saveMemberReservation(1L, RESERVATION_ADD_REQUEST_WITH_INVALID_PAYMENTS))
                .isInstanceOf(IllegalRequestException.class);
    }

    @DisplayName("결제가 5xx 에러로 인해 승인되지 않으면 멤버의 예약 저장에 실패한다")
    @Test
    void should_throw_exception_when_reservation_not_confirmed_payments_5xx_error() {
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(
                () -> reservationService.saveMemberReservation(1L, RESERVATION_ADD_REQUEST_WITH_INVALID_PAYMENTS))
                .isInstanceOf(InternalServerException.class);
    }

    @DisplayName("결제가 승인된 후 멤버의 예약 저장 프로세스가 수행된다")
    @Test
    void should_save_member_reservation_when_payment_is_confirmed() {
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withStatus(HttpStatus.OK));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(SAVED_RESERVATION_1);
        when(reservationRepository.findById(any(Long.class))).thenReturn(Optional.of(SAVED_RESERVATION_1));

        reservationService.saveMemberReservation(1L, RESERVATION_ADD_REQUEST_WITH_VALID_PAYMENTS);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}
