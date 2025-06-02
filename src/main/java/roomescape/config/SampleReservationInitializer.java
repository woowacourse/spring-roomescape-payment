package roomescape.config;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.RoomEscapeInformation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.RoomEscapeInformationRepository;
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
    private final RoomEscapeInformationRepository roomEscapeInformationRepository;

    @Transactional
    @Override
    public void run(String... args) {
        System.out.println("======================================");
        LocalDate baseDate = LocalDate.now().minusDays(5);

        saveInformation(baseDate, 1L, 1L);
        saveInformation(baseDate, 1L, 2L);
        saveInformation(baseDate, 1L, 3L);
        saveInformation(baseDate, 1L, 4L);
        saveInformation(baseDate, 1L, 5L);
        saveInformation(baseDate.plusDays(1), 1L, 1L);
        saveInformation(baseDate.plusDays(2), 1L, 1L);
        saveInformation(baseDate.plusDays(3), 1L, 1L);
        saveInformation(baseDate.plusDays(4), 1L, 1L);

        saveReservation(1L, 2L);
        saveReservation(2L, 3L);
        saveReservation(3L, 4L);
        saveReservation(4L, 5L);
        saveReservation(1L, 6L);
        saveReservation(2L, 7L);
        saveReservation(3L, 8L);
        saveReservation(4L, 9L);

        saveReservation(2L, 1L);
        saveWaiting(1L, 1L);
        saveWaiting(3L, 1L);
        saveWaiting(4L, 1L);
    }

    private void saveInformation(LocalDate date, Long timeId, Long themeId) {
        ReservationTime time = timeRepository.findById(timeId).orElseThrow(() -> new IllegalArgumentException("timeId 찾기 오류, timeId: " + timeId));
        Theme theme = themeRepository.findById(themeId).orElseThrow(() -> new IllegalArgumentException("themeId 찾기 오류, themeId: " + themeId));

        RoomEscapeInformation info = RoomEscapeInformation.builder()
                .date(date)
                .time(time)
                .theme(theme)
                .build();
        roomEscapeInformationRepository.save(info);
    }

    private void saveReservation(Long memberId, Long infoId) {
        Reservation reservation = Reservation.builder()
                .member(memberRepository.findById(memberId).get())
                .roomEscapeInformation(roomEscapeInformationRepository.findById(infoId).get())
                .build();
        reservationRepository.save(reservation);
    }

    private void saveWaiting(Long memberId, Long infoId) {
        WaitingReservation waiting = WaitingReservation.builder()
                .member(memberRepository.findById(memberId).get())
                .roomEscapeInformation(roomEscapeInformationRepository.findById(infoId).get())
                .build();
        waitingRepository.save(waiting);
    }
}
