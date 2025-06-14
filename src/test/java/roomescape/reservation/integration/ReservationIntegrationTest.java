package roomescape.reservation.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.global.error.exception.ConflictException;
import roomescape.global.error.exception.InvalidReservationException;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationAdminCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationFindFilteredRequest;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @MockitoBean
    private PaymentService paymentService;

    private LocalDate tomorrow = LocalDate.now().plusDays(1);

    private Member member;

    private ReservationTime time;

    private Theme theme;

    private Payment payment;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createDefault();
        memberRepository.save(member);

        time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        reservationTimeRepository.save(time);

        theme = ThemeFixture.createDefault();
        themeRepository.save(theme);

        payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        given(paymentService.confirmPayment(any(), any(), any()))
                .willReturn(payment);
    }

    @Test
    @DisplayName("사용자 예약 생성 - 성공")
    void createReservation() {
        // given
        var request = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );

        // when
        var response = reservationService.createReservation(member.getId(), request);

        // then
        assertAll(
                () -> assertThat(response.date()).isEqualTo(tomorrow),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.memberName()).isEqualTo(member.getName()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("관리자 예약 생성 - 성공")
    void createReservationByAdmin() {
        // given
        var request = new ReservationAdminCreateRequest(tomorrow, theme.getId(), time.getId(), member.getId());

        // when
        var response = reservationService.createReservationByAdmin(request);

        // then
        assertAll(
                () -> assertThat(response.date()).isEqualTo(tomorrow),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.memberName()).isEqualTo(member.getName()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("관리자 예약 생성 - 과거 시간으로 예약시 실패")
    void createReservationWithPastDate() {
        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        var request = new ReservationCreateRequest(
                yesterday,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(member.getId(), request))
                .isInstanceOf(InvalidReservationException.class)
                .hasMessage("과거 날짜는 예약할 수 없습니다.");
    }

    @Test
    @DisplayName("관리자 예약 생성 - 예약 시간이 이미 예약된 경우 실패")
    void createReservationWithDuplicateTime() {
        // given
        var request = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        reservationService.createReservation(member.getId(), request);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(member.getId(), request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("중복된 예약입니다.");
    }

    @Test
    @DisplayName("모든 예약 조회")
    void getAllReservations() {
        // given
        var firstReservationRequest = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        reservationService.createReservation(member.getId(), firstReservationRequest);

        Payment secondPayment = PaymentFixture.createDefault();
        paymentRepository.save(secondPayment);
        given(paymentService.confirmPayment(any(), any(), any()))
                .willReturn(secondPayment);
        var secondReservationRequest = new ReservationCreateRequest(
                tomorrow.plusDays(1),
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        reservationService.createReservation(member.getId(), secondReservationRequest);

        // when
        var responses = reservationService.getAllReservations();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("필터링 예약 조회 - 성공")
    void getFilteredReservations() {
        // given
        var request = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        reservationService.createReservation(member.getId(), request);

        LocalDate startDate = tomorrow.minusDays(1);
        LocalDate endDate = tomorrow.plusDays(1);
        var filterRequest = new ReservationFindFilteredRequest(
                theme.getId(),
                member.getId(),
                startDate,
                endDate
        );

        // when
        var responses = reservationService.getFilteredReservations(filterRequest);

        // then
        var response = responses.getFirst();
        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.date()).isEqualTo(tomorrow),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.memberName()).isEqualTo(member.getName()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("예약 삭제 - 성공")
    void deleteReservation() {
        // given
        var request = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        var response = reservationService.createReservation(member.getId(), request);

        // when
        reservationService.deleteReservation(response.id());

        // then
        var reservations = reservationService.getAllReservations();
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("사용자 예약 기록 조회 - 성공")
    void getReservationsByMember() {
        // given
        var request = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        reservationService.createReservation(member.getId(), request);

        // when
        var response = reservationService.getReservationsByMember(member.getId());

        // then
        assertAll(
                () -> assertThat(response).hasSize(1),
                () -> assertThat(response.get(0).theme()).isEqualTo(theme.getName()),
                () -> assertThat(response.get(0).date()).isEqualTo(tomorrow),
                () -> assertThat(response.get(0).time()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.get(0).payment().paymentKey()).isEqualTo(payment.getPaymentKey()),
                () -> assertThat(response.get(0).payment().amount()).isEqualTo(payment.getAmount())
        );
    }
}
