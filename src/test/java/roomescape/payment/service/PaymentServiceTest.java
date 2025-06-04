package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.config.RestClientConfig;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@ActiveProfiles("test")
@DataJpaTest
@Import({RestClientConfig.class, PaymentService.class})
class PaymentServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RestClient.Builder builder;
    @Autowired
    private PaymentService paymentService;

    @DisplayName("결제 정보를 저장한다.")
    @Test
    void savePayment() {
        // given
        Member member = memberRepository.save(new Member("피케이", "pk@woowa.com", "12341234", Role.ADMIN));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Theme theme = themeRepository.save(new Theme(1L, "테마1", "테마1", "www.m.com"));
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), reservationTime, theme)
        );

        // when
        paymentService.savePayment(reservation.getId(), new PaymentRequest("paymentKey", "orderId", 1000L));

        // then
        assertThat(paymentRepository.findAll()).hasSize(1);
    }

}
