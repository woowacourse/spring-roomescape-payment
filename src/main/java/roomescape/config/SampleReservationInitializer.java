package roomescape.config;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.RegistrationSlot;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Component
@Profile("local")
@RequiredArgsConstructor
public class SampleReservationInitializer implements CommandLineRunner {

    private final ReservationRepository reservationRepository;
    private final WaitingReservationRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public void run(String... args) {
        LocalDate baseDate = LocalDate.now().minusDays(3);

        saveReservation(3L, baseDate, 3L, 9L);
        saveReservation(7L, baseDate, 9L, 7L);
        saveReservation(2L, baseDate.minusDays(1), 2L, 16L);
        saveReservation(9L, baseDate.plusDays(3), 3L, 19L);
        saveReservation(5L, baseDate.plusDays(2), 10L, 5L);
        saveReservation(1L, baseDate, 9L, 16L);
        saveReservation(6L, baseDate.minusDays(1), 7L, 20L);
        saveReservation(4L, baseDate.plusDays(2), 7L, 11L);
        saveReservation(8L, baseDate.plusDays(3), 7L, 13L);
        saveReservation(10L, baseDate, 5L, 4L);
        saveReservation(2L, baseDate.plusDays(1), 4L, 2L);
        saveReservation(5L, baseDate.plusDays(4), 8L, 15L);
        saveReservation(9L, baseDate.plusDays(5), 1L, 7L);
        saveReservation(7L, baseDate.minusDays(1), 6L, 11L);
        saveReservation(1L, baseDate.plusDays(4), 2L, 5L);
        saveReservation(3L, baseDate.plusDays(3), 10L, 18L);
        saveReservation(6L, baseDate.plusDays(2), 5L, 20L);
        saveReservation(8L, baseDate.plusDays(1), 9L, 3L);
        saveReservation(4L, baseDate.plusDays(5), 7L, 7L);
        saveReservation(10L, baseDate.plusDays(4), 11L, 13L);

        saveReservation(2L, baseDate, 1L, 1L);
        saveWaiting(1L, baseDate, 1L, 1L);
        saveWaiting(3L, baseDate, 1L, 1L);
        saveWaiting(4L, baseDate, 1L, 1L);
        saveWaiting(5L, baseDate, 1L, 1L);
    }

    private void saveReservation(Long memberId, LocalDate date, Long timeId, Long themeId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        ReservationTime time = timeRepository.findById(timeId).orElseThrow();
        Theme theme = themeRepository.findById(themeId).orElseThrow();

        RegistrationSlot registrationSlot = RegistrationSlot.builder()
                .theme(theme)
                .date(date)
                .time(time)
                .build();

        Reservation reservation = Reservation.createNew(member, registrationSlot);
        reservationRepository.save(reservation);

        Payment payment = Payment.builder()
                .amount(10000L)
                .status(PaymentStatus.COMPLETED)
                .member(member)
                .reservation(reservation)
                .paymentKey("test-paymentKe-" + UUID.randomUUID().toString().substring(0, 4))
                .orderId("test-orderId-" + UUID.randomUUID().toString().substring(0, 4))
                .build();
        paymentRepository.save(payment);
    }

    private void saveWaiting(Long memberId, LocalDate date, Long timeId, Long themeId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        ReservationTime time = timeRepository.findById(timeId).orElseThrow();
        Theme theme = themeRepository.findById(themeId).orElseThrow();

        RegistrationSlot registrationSlot = RegistrationSlot.builder()
                .theme(theme)
                .date(date)
                .time(time)
                .build();

        WaitingReservation waitingReservation = WaitingReservation.builder()
                .member(member)
                .registrationSlot(registrationSlot)
                .build();
        waitingRepository.save(waitingReservation);
    }
}
