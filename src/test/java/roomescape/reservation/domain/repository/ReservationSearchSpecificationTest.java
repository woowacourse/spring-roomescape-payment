package roomescape.reservation.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;

@DataJpaTest
class ReservationSearchSpecificationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * 시간은 모두 현재 시간(LocalTime.now()), 테마, 회원은 동일 확정된 예약은 오늘, 결제 대기인 예약은 어제, 대기 상태인 예약은 내일
     */
    // 현재 시간으로 확정 예약
    private Reservation reservation1;
    // 확정되었으나 결제 대기인 하루 전 예약
    private Reservation reservation2;
    // 대기 상태인 내일 예약
    private Reservation reservation3;

    @BeforeEach
    void setUp() {
        LocalDateTime dateTime = LocalDateTime.now();
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));
        ReservationTime time = timeRepository.save(new ReservationTime(dateTime.toLocalTime()));
        Theme theme = themeRepository.save(new Theme("name", "description", "thumbnail"));

        reservation1 = reservationRepository.save(
                new Reservation(dateTime.toLocalDate(), time, theme, member, ReservationStatus.CONFIRMED));
        reservation2 = reservationRepository.save(
                new Reservation(dateTime.toLocalDate().minusDays(1), time, theme, member,
                        ReservationStatus.CONFIRMED_PAYMENT_REQUIRED));
        reservation3 = reservationRepository.save(
                new Reservation(dateTime.toLocalDate().plusDays(1), time, theme, member, ReservationStatus.WAITING));
    }

    @Test
    @DisplayName("동일한 테마의 예약을 찾는다.")
    void searchByThemeId() {
        // given
        Long themeId = reservation1.getTheme().getId();
        Specification<Reservation> spec = new ReservationSearchSpecification().sameThemeId(themeId).build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1, reservation2, reservation3);
    }

    @Test
    @DisplayName("동일한 회원의 예약을 찾는다.")
    void searchByMemberId() {
        // given
        Long memberId = reservation1.getMember().getId();
        Specification<Reservation> spec = new ReservationSearchSpecification().sameMemberId(memberId).build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1, reservation2, reservation3);
    }

    @Test
    @DisplayName("동일한 시간의 예약을 찾는다.")
    void searchByTimeId() {
        // given
        Long timeId = reservation1.getReservationTime().getId();
        Specification<Reservation> spec = new ReservationSearchSpecification().sameTimeId(timeId).build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1, reservation2, reservation3);
    }

    @Test
    @DisplayName("동일한 날짜의 예약을 찾는다.")
    void searchByDate() {
        // given
        LocalDate date = reservation1.getDate();
        Specification<Reservation> spec = new ReservationSearchSpecification().sameDate(date).build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1);
    }

    @Test
    @DisplayName("확정 상태인 예약을 찾는다.")
    void searchConfirmedReservation() {
        // given
        Specification<Reservation> spec = new ReservationSearchSpecification().confirmed().build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1, reservation2);
    }

    @Test
    @DisplayName("대기 중인 예약을 찾는다.")
    void searchWaitingReservation() {
        // given
        Specification<Reservation> spec = new ReservationSearchSpecification().waiting().build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation3);
    }

    @Test
    @DisplayName("특정 날짜 이후의 예약을 찾는다.")
    void searchDateStartFrom() {
        // given : 어제 이후의 예약을 조회하면, 모든 예약이 조회되어야 한다.
        LocalDate date = LocalDate.now().minusDays(1L);
        Specification<Reservation> spec = new ReservationSearchSpecification().dateStartFrom(date).build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1, reservation2, reservation3);
    }

    @Test
    @DisplayName("특정 날짜 이전의 예약을 찾는다.")
    void searchDateEndAt() {
        // given : 내일 이전의 예약을 조회하면, 모든 예약이 조회되어야 한다.
        LocalDate date = LocalDate.now().plusDays(1L);
        Specification<Reservation> spec = new ReservationSearchSpecification().dateEndAt(date).build();

        // when
        List<Reservation> found = reservationRepository.findAll(spec);

        // then
        assertThat(found).containsExactly(reservation1, reservation2, reservation3);
    }
}
