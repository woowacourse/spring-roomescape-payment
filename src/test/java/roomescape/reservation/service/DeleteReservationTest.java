package roomescape.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import roomescape.auth.service.dto.LoginMember;
import roomescape.common.exception.ForbiddenException;
import roomescape.common.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@Import({ReservationCommandService.class})
class DeleteReservationTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationCommandService reservationCommandService;

    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("권한이 멤버인 경우 다른 사람의 예약을 삭제할 수 없다.")
    @Test
    void cannotDeleteOtherReservation() {
        // given
        Member me = new Member("me", "test@test.com", "12341234", Role.MEMBER);
        Member other = new Member("other", "test@test.com", "12341234", Role.MEMBER);
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("theme", "desc", "thumbnail");

        Reservation myReservation = new Reservation(me, LocalDate.now().plusDays(1), time, theme, ReservationStatus.CONFIRMED);

        entityManager.persist(me);
        entityManager.persist(other);
        entityManager.persist(time);
        entityManager.persist(theme);
        entityManager.persist(myReservation);

        // when & then
        assertThatThrownBy(() -> {
            reservationCommandService.cancel(myReservation.getId(), LoginMember.of(other));
        }).isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("권한이 어드민인 경우 예약을 삭제한다")
    @Test
    void test7() {
        // given
        Theme savedTheme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));

        LocalTime time = LocalTime.of(8, 0);
        ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));
        Member member = new Member("포스티", "test@test.com", "12341234", Role.MEMBER);
        Member savedMember = memberRepository.save(member);

        LocalDate date = nextDay();
        Reservation savedReservation = reservationRepository.save(
                new Reservation(savedMember, date, savedTime, savedTheme, ReservationStatus.CONFIRMED));

        Long id = savedReservation.getId();

        // then
        assertThatCode(() -> reservationCommandService.cancel(id, LoginMember.of(member)))
                .doesNotThrowAnyException();
    }

    @DisplayName("예약이 존재하지 않으면 예외를 반환한다.")
    @Test
    void test8() {
        Member member = new Member("어드민", "test@test.com", "12341234", Role.ADMIN);
        Long id = 1L;
        assertThatThrownBy(() -> reservationCommandService.cancel(id, LoginMember.of(member)))
                .isInstanceOf(NotFoundException.class);
    }

    private LocalDate nextDay() {
        return LocalDate.now().plusDays(1);
    }
}
