package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.service.dto.ReservationPaymentRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.UserReservationResponse;

@Transactional
class ReservationPaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReservationPaymentService reservationPaymentService;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("예약과 결제 내역을 저장한다.")
    @Test
    void saveReservation() {
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(member.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");
        ReservationResponse reservationResponse = reservationPaymentService.saveReservationWithPayment(reservationPaymentRequest);

        assertAll(
                () -> assertThat(reservationResponse.member().name()).isEqualTo("고구마"),
                () -> assertThat(reservationResponse.date()).isEqualTo(LocalDate.parse("2025-11-11")),
                () -> assertThat(reservationResponse.time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservationResponse.time().startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(reservationResponse.theme().id()).isEqualTo(theme.getId()),
                () -> assertThat(reservationResponse.theme().name()).isEqualTo(theme.getName()),
                () -> assertThat(reservationResponse.theme().description()).isEqualTo(theme.getDescription()),
                () -> assertThat(reservationResponse.theme().thumbnail()).isEqualTo(theme.getThumbnail()),
                () -> assertThat(paymentRepository.findByReservationIdAndMemberIdAndStatus(reservationResponse.reservationId(), member.getId(),
                        PaymentStatus.DONE)).isPresent()
        );
    }

    @DisplayName("내 예약을 조회하면 예약, 결제 대기, 예약 대기 상태를 표시한다.")
    @Test
    void findAllMyReservations() {
        // given // when
        List<UserReservationResponse> allUserReservation = reservationPaymentService.findMyAllReservationWithPayment(1L,
                LocalDate.parse("2024-05-30"));

        // then
        assertThat(allUserReservation).hasSize(3)
                .extracting("id", "status", "rank")
                .containsExactly(
                        tuple(14L, "결제 대기", 0L),
                        tuple(2L, "예약대기", 2L),
                        tuple(3L, "예약대기", 1L)
                );
    }
}
