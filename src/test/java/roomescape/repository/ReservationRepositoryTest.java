package roomescape.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.MemberRole;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.*;
import roomescape.repository.impl.*;
import roomescape.repository.jpa.*;
import roomescape.test_util.RepositoryTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ReservationRepositoryTest extends RepositoryTest {

    private ReservationRepository reservationRepository;

    private MemberRepository memberRepository;
    private ReservationThemeRepository reservationThemeRepository;
    private ReservationTimeRepository reservationTimeRepository;
    private ReservationItemRepository reservationItemRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private ReservationThemeJpaRepository reservationThemeJpaRepository;
    @Autowired
    private ReservationTimeJpaRepository reservationTimeJpaRepository;
    @Autowired
    private ReservationItemJpaRepository reservationItemJpaRepository;

    private Member member;
    private Member member2;
    private Member member3;
    private ReservationTime time;
    private ReservationTheme theme;
    private ReservationItem reservationItem;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservationRepository = new ReservationRepositoryImpl(reservationJpaRepository);
        memberRepository = new MemberRepositoryImpl(memberJpaRepository);
        reservationThemeRepository = new ReservationThemeRepositoryImpl(reservationThemeJpaRepository, reservationJpaRepository);
        reservationTimeRepository = new ReservationTimeRepositoryImpl(reservationTimeJpaRepository, reservationJpaRepository);
        reservationItemRepository = new ReservationItemRepositoryImpl(reservationItemJpaRepository);

        member = memberRepository.save(
                new Member("test@example.com", "testPassword", "test", MemberRole.USER)
        );
        member2 = memberRepository.save(
                new Member("test2@example.com", "testPassword2", "test2", MemberRole.USER)
        );
        member3 = memberRepository.save(
                new Member("test3@example.com", "testPassword3", "test3", MemberRole.USER)
        );
        time = reservationTimeRepository.save(
                new ReservationTime(LocalTime.now())
        );
        theme = reservationThemeRepository.save(
                new ReservationTheme("Theme", "Description", "Thumbnail")
        );
        reservationItem = reservationItemRepository.save(new ReservationItem(LocalDate.now().plusDays(1), time, theme));
        reservation = reservationRepository.save(
                new Reservation(member, reservationItem, ReservationStatus.ACCEPTED)
        );
    }

    @Test
    @DisplayName("사용자 아이디를 통해 해당 사용자의 예약 내역들을 조회한다")
    void findReservationsByMemberIdTest() {
        // given
        Long memberId = member.getId();

        // when
        final List<Reservation> reservations = reservationRepository.findByMemberId(memberId);

        // then
        assertAll(
                () -> assertThat(reservations).hasSize(1),
                () -> assertThat(reservations.getFirst().getReservationItem().getTime().getId()).isEqualTo(time.getId()),
                () -> assertThat(reservations.getFirst().getReservationItem().getTheme().getId()).isEqualTo(theme.getId())
        );
    }
}
