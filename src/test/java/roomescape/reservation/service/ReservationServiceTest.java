package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import roomescape.config.TestConfig;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.exception.ReservationNotFoundException;
import roomescape.reservation.fixture.TestFixture;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class ReservationServiceTest {

    private static final LocalDate futureDate = TestFixture.makeFutureDate();

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    private ReservationService reservationService;
    private ReservationTime time;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(reservationRepository);

        ReservationTime time2 = ReservationTime.withUnassignedId(LocalTime.of(9, 0));
        time = reservationTimeRepository.save(time2);
        theme = themeRepository.save(TestFixture.makeTheme(1L));
        member = memberRepository.save(TestFixture.makeMember());
    }

    @Test
    void findReservations_shouldReturnAllReservations() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<ReservationResponse> result = reservationService.findReservations(null, null, null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findReservations_shouldFilterByThemeId() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<ReservationResponse> result = reservationService.findReservations(theme.getId(), null, null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findReservations_shouldFilterByMemberId() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<ReservationResponse> result = reservationService.findReservations(null, member.getId(), null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findReservations_shouldFilterByDateRange() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<ReservationResponse> result = reservationService.findReservations(
                null, null, futureDate, futureDate.plusDays(1));
        assertThat(result).hasSize(1);
    }

    @Test
    void findMyReservations_shouldReturnMemberReservations() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<Reservation> result = reservationService.findMyReservations(
                new UserInfo(member.getId(), MemberRole.USER));
        assertThat(result).hasSize(1);
    }

    @Test
    void findById_shouldReturnReservation() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservation = reservationRepository.save(reservation);

        Reservation found = reservationService.findById(reservation.getId());
        assertAll(
                () -> assertThat(found).isNotNull(),
                () -> assertThat(found.getDate()).isEqualTo(futureDate),
                () -> assertThat(found.getTime().getStartAt()).isEqualTo(LocalTime.of(9, 0)),
                () -> assertThat(found.getTheme().getName()).isEqualTo("추리")
        );
    }

    @Test
    void findById_shouldThrowException_whenReservationNotFound() {
        assertThatThrownBy(() -> reservationService.findById(999L))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("요청한 id와 일치하는 예약 정보가 없습니다.");
    }

    @Test
    void delete_shouldRemoveReservation() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservation = reservationRepository.save(reservation);

        reservationService.delete(reservation.getId());
        assertThat(reservationRepository.findAll()).isEmpty();
    }
}