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
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.exception.ReservationNotFoundException;
import roomescape.reservation.fixture.TestFixture;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.dto.ReservationWithPayment;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.ThemeNotFoundException;
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
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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
        reservationService = new ReservationService(reservationRepository, reservationTimeRepository, themeRepository,
                memberRepository, paymentRepository);

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

        List<Reservation> result = reservationService.getReservations(null, null, null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findReservations_shouldFilterByThemeId() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<Reservation> result = reservationService.getReservations(theme.getId(), null, null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findReservations_shouldFilterByMemberId() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<Reservation> result = reservationService.getReservations(null, member.getId(), null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findReservations_shouldFilterByDateRange() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<Reservation> result = reservationService.getReservations(
                null, null, futureDate, futureDate.plusDays(1));
        assertThat(result).hasSize(1);
    }

    @Test
    void findMyReservations_shouldReturnMemberReservations() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        List<ReservationWithPayment> result = reservationService.findMyReservations(
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

    @Test
    void isReservationExists_returnTrue() {
        ReservationRequest request = new ReservationRequest(futureDate, time.getId(), theme.getId());
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        boolean reservationExists = reservationService.isReservationExists(request);

        assertThat(reservationExists).isTrue();
    }

    @Test
    void isReservationExists_returnFalse() {
        ReservationRequest request = new ReservationRequest(futureDate.plusDays(1), time.getId(), theme.getId());
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Reservation reservation = Reservation.createUpcomingReservationWithUnassignedId(member, info);
        reservationRepository.save(reservation);

        boolean reservationExists = reservationService.isReservationExists(request);

        assertThat(reservationExists).isFalse();
    }

    @Test
    void createReservation_exception_whenNoReservationTime() {
        ReservationRequest request = new ReservationRequest(futureDate.plusDays(1), time.getId() + 1, theme.getId());

        assertThatThrownBy(() -> reservationService.createReservation(request, member.getId()))
                .isInstanceOf(ReservationTimeNotFoundException.class);
    }

    @Test
    void createReservation_exception_whenNoMember() {
        ReservationRequest request = new ReservationRequest(futureDate.plusDays(1), time.getId(), theme.getId());

        assertThatThrownBy(() -> reservationService.createReservation(request, member.getId() + 1))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void createReservation_exception_whenNoTheme() {
        ReservationRequest request = new ReservationRequest(futureDate.plusDays(1), time.getId(), theme.getId() + 1);

        assertThatThrownBy(() -> reservationService.createReservation(request, member.getId()))
                .isInstanceOf(ThemeNotFoundException.class);
    }

    @Test
    void createReservation_success() {
        ReservationRequest request = new ReservationRequest(futureDate.plusDays(1), time.getId(), theme.getId());

        Reservation reservation = reservationService.createReservation(request, member.getId());

        assertThat(reservation.getId()).isNotNull();
    }
}