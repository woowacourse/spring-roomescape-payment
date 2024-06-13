package roomescape.reservation.service.component;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.JOJO_RESERVATION;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TODAY;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.util.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.service.dto.request.ReservationPaymentSaveRequest;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class ReservationComponentServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationComponentService reservationComponentService;

    @MockBean
    private PaymentService paymentService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("예약 시, 중복된 예약이 있다면 예외가 발생한다.")
    @Test
    void saveWithDuplicateReservation() {
        // given
        Theme horror = themeRepository.save(HORROR_THEME);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Member jojo = memberRepository.save(MEMBER_JOJO);

        ReservationPaymentSaveRequest saveRequest = new ReservationPaymentSaveRequest(
                jojo.getId(), TODAY, horror.getId(), hour10.getId(), "paymentKey", "orderId", 1000L);

        when(paymentService.confirm(any(), any())).thenReturn(
                new Payment(1L, "paymentKey", "orderId", 1000L, JOJO_RESERVATION));

        reservationComponentService.saveWithPayment(saveRequest);

        // when & then
        assertThatThrownBy(() -> reservationComponentService.saveWithPayment(saveRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
