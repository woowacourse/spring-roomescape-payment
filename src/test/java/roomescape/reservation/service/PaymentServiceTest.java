package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;

import roomescape.config.ClientConfig;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Import(value = ClientConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository timeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanUp();
    }

    @Test
    @DisplayName("결제 정보를 바탕으로 구매를 진행한다.")
    void purchaseShouldPaymentWithSpecificInfo() {
        // given
        Member member = new Member("name", "aa@aa.aa", "aa");
        Theme theme = new Theme("n", "D", "t");
        ReservationTime time = new ReservationTime(LocalTime.of(1, 0));
        Reservation reservation = new Reservation(member, LocalDate.now().plusDays(1), theme, time, Status.SUCCESS);
        memberRepository.save(member);
        themeRepository.save(theme);
        timeRepository.save(time);
        reservationRepository.save(reservation);

        PaymentRequest request = new PaymentRequest(1000L, "orderId", "paymentKey");

        // when
        paymentService.purchase(request, reservation.getId());

        // then
        assertThat(paymentRepository.findById(reservation.getId()))
                .isPresent();
    }

}
