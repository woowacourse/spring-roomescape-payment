package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.PaymentRequest;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.infra.PaymentRestClient;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentClient paymentClient;

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAll();
        reservationTimeRepository.deleteAll();
        themeRepository.deleteAll();
        memberRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("결제가 정상 처리되면, 결제 정보를 저장한다.")
    void savePayment() {
        // given
        Member member = memberRepository.save(MemberFixture.DEFAULT_MEMBER);
        Theme theme = themeRepository.save(ThemeFixture.DEFAULT_THEME);
        ReservationTime time = reservationTimeRepository.save(ReservationTimeFixture.DEFAULT_TIME);
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationStatus status = ReservationStatus.RESERVED_UNPAID;

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .member(member)
                .date(date)
                .time(time)
                .theme(theme)
                .status(status)
                .build());

        PaymentRequest request = new PaymentRequest(reservation.getId(), "paymentKey", "WTESTzzzzz", 1000L);
        given(paymentRestClient.requestPaymentApproval(any(PaymentRequest.class)))
                .willReturn(new Payment(null, "paymentKey", "WTESTzzzzz", 1000L));

        // when & then
        assertThatCode(() -> paymentService.savePaymentAndUpdateReservationStatus(request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("결제가 실패하면, 예약의 결제 정보를 갱신하지 않는다.")
    void failPaymentThenReservationPaymentInfoNotUpdated() {
        // given
        Member member = memberRepository.save(MemberFixture.DEFAULT_MEMBER);
        Theme theme = themeRepository.save(ThemeFixture.DEFAULT_THEME);
        ReservationTime time = reservationTimeRepository.save(ReservationTimeFixture.DEFAULT_TIME);
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationStatus status = ReservationStatus.RESERVED_UNPAID;

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .member(member)
                .date(date)
                .time(time)
                .theme(theme)
                .status(status)
                .build());

        given(paymentRestClient.requestPaymentApproval(any(PaymentRequest.class)))
                .willThrow(new RuntimeException());

        // when
        Reservation foundReservation = reservationRepository.findById(reservation.getId()).get();

        // then
        assertAll(
                () -> assertThat(foundReservation.getStatus()).isEqualTo(ReservationStatus.RESERVED_UNPAID),
                () -> assertThat(foundReservation.getPayment()).isNull()
        );
    }
}
