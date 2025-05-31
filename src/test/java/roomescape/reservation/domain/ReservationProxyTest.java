package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.fixture.entity.ReservationFixture;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.repository.ReservationTimeRepository;

@SpringBootTest
public class ReservationProxyTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = ReservationFixture.create();
        memberRepository.save(reservation.getReserver());
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
    }

    @Test
    void test1() {

        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();

        Reservation reservation1 = reservationRepository.findById(id).orElseThrow();
        Reservation proxy = reservationRepository.getReferenceById(id);

        Set<Reservation> reservations = new HashSet<>();

        reservations.add(reservation1);
        reservations.add(proxy);

        assertThat(reservations).hasSize(1);
    }
}
