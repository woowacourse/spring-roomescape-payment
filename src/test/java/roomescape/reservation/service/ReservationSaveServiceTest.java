package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TODAY;
import static roomescape.Fixture.JOJO_RESERVATION;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.service.dto.request.ReservationPaymentRequest;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class ReservationSaveServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationService reservationService;

    @MockBean
    private PaymentService paymentService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("예약 시, 중복된 예약이 있다면 예외가 발생한다.")
    @Test
    void duplicateReservationExceptionTest() {
        Theme horror = themeRepository.save(HORROR_THEME);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Member jojo = memberRepository.save(MEMBER_JOJO);

        ReservationPaymentRequest saveRequest = new ReservationPaymentRequest(
                jojo.getId(), TODAY, horror.getId(), hour10.getId(), "paymentKey", "orderId", 1000L
        );

        when(paymentService.confirm(any(), any())).thenReturn(
                new Payment(1L, "paymentKey", "orderId", 1000L, JOJO_RESERVATION));

        reservationService.save(saveRequest);

        assertThatThrownBy(() -> reservationService.save(saveRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
