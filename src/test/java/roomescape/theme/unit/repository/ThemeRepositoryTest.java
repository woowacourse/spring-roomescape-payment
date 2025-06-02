package roomescape.theme.unit.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.payment.entity.Payment;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationTime;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ThemeRepository themeRepository;

    @Test
    @DisplayName("")
    void findPopularDescendingUpTo() {
        // given
        var reservationTime1 = new ReservationTime(LocalTime.of(10, 0));
        var reservationTime2 = new ReservationTime(LocalTime.of(11, 0));
        var theme1 = new Theme("테마1", "설명1", "썸네일1");
        var theme2 = new Theme("테마2", "설명2", "썸네일2");
        var member1 = new Member("미소", "miso@email.com", "miso", RoleType.USER);
        var member2 = new Member("훌라", "hula@email.com", "hula", RoleType.USER);
        var date = LocalDate.now().plusDays(1);
        var payment1 = new Payment("any", "any", 1L, "any");
        var payment2 = new Payment("any", "any", 1L, "any");
        var payment3 = new Payment("any", "any", 1L, "any");
        var reservation1 = new Reservation(date, reservationTime1, theme2, member1, payment1);
        var reservation2 = new Reservation(date, reservationTime2, theme2, member2, payment2);
        var reservation3 = new Reservation(date, reservationTime2, theme1, member2, payment3);
        entityManager.persist(reservationTime1);
        entityManager.persist(reservationTime2);
        entityManager.persist(theme1);
        entityManager.persist(theme2);
        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(payment1);
        entityManager.persist(payment2);
        entityManager.persist(payment3);
        entityManager.persist(reservation1);
        entityManager.persist(reservation2);
        entityManager.persist(reservation3);

        // when
        List<Theme> themes = themeRepository.findPopularDescendingUpTo(
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(2), 10
        );

        // then
        assertAll(
                () -> assertThat(themes.get(0)).isEqualTo(theme2),
                () -> assertThat(themes.get(1)).isEqualTo(theme1)
        );
    }
}
