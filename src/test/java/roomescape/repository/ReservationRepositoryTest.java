package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("모든 예약을 조회한다.")
    @Test
    void should_get_all_reservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        assertThat(reservations).hasSize(2);
    }

    @DisplayName("특정 날짜와 테마에 해당하는 시간을 조회한다.")
    @Test
    void should_search_reservation_by_condition() {
        Theme theme = themeRepository.findById(1L).get();
        List<Reservation> reservations = reservationRepository.findAllByDateAndTheme(now().plusDays(1), theme);

        assertThat(reservations).hasSize(1);
    }

    @DisplayName("예약을 추가한다")
    @Test
    void should_add_reservation() {
        LocalDate day = LocalDate.now().plusDays(2);
        ReservationTime time = reservationTimeRepository.findById(1L).get();
        Theme theme = themeRepository.findById(1L).get();
        Member member = memberRepository.findById(1L).get();

        reservationRepository.save(Reservation.paymentWaitingStatusOf(day, time, theme, member));

        assertThat(reservationRepository.count()).isEqualTo(3);
    }

    @DisplayName("예약을 삭제한다")
    @Test
    void should_delete_reservation() {
        reservationRepository.deleteById(1L);

        assertThat(reservationRepository.count()).isEqualTo(1);
    }

    @DisplayName("아이디에 해당하는 예약이 존재하면 참을 반환한다.")
    @Test
    void should_return_true_when_id_exist() {
        boolean exists = reservationRepository.existsById(1L);

        assertThat(exists).isTrue();
    }

    @DisplayName("예약시간에 해당하는 예약이 존재하면 참을 반환한다.")
    @Test
    void should_return_true_when_time_exist() {
        ReservationTime time = reservationTimeRepository.findById(1L).get();

        boolean exists = reservationRepository.existsByTime(time);

        assertThat(exists).isTrue();
    }

    @DisplayName("날짜, 시간, 테마에 해당하는 예약이 존재하면 참을 반환한다.")
    @Test
    void should_return_reservation_count_when_give_date_and_time_and_theme() {
        LocalDate day = LocalDate.now().plusDays(1);
        ReservationTime time = reservationTimeRepository.findById(1L).get();
        Theme theme = themeRepository.findById(1L).get();

        boolean exists = reservationRepository.existsByDateAndTimeAndTheme(day, time, theme);
        assertThat(exists).isTrue();
    }

    @DisplayName("사용자 아이디에 해당하는 예약을 반환한다.")
    @Test
    void should_return_member_reservations() {
        Member member = memberRepository.findById(1L).get();

        List<Reservation> reservations = reservationRepository.findAllByMember(member);

        assertThat(reservations).hasSize(2);
    }

    @DisplayName("조건에 맞는 예약을 반환한다.")
    @Sql(scripts = {"/initialize_table.sql", "/reservation_data.sql"})
    @Test
    void should_return_reservations_when_give_conditions() {
        LocalDate day1 = LocalDate.now().plusDays(1);
        LocalDate day2 = LocalDate.now().plusDays(5);
        LocalDate middle = LocalDate.now().plusDays(3);
        Theme theme = themeRepository.findById(1L).get();
        Member member = memberRepository.findById(1L).get();

        List<Reservation> reservations = reservationRepository.findByConditions(theme, member, day1, middle);
        assertThat(reservations).hasSize(1);
        reservations = reservationRepository.findByConditions(theme, member, day1, null);
        assertThat(reservations).hasSize(2);
        reservations = reservationRepository.findByConditions(theme, member, null, day2);
        assertThat(reservations).hasSize(2);
        reservations = reservationRepository.findByConditions(theme, null, day1, day2);
        assertThat(reservations).hasSize(4);
        reservations = reservationRepository.findByConditions(null, member, day1, day2);
        assertThat(reservations).hasSize(4);
    }

    @DisplayName("날짜, 시간, 테마, 멤버에 해당하는 예약이 존재하면 참을 반환한다.")
    @Test
    void should_return_reservation_count_when_give_date_and_time_and_theme_and_member() {
        LocalDate day = LocalDate.now().plusDays(1);
        ReservationTime time = reservationTimeRepository.findById(1L).get();
        Theme theme = themeRepository.findById(1L).get();
        Member member = memberRepository.findById(1L).get();

        boolean exists = reservationRepository.existsReservationByThemeAndDateAndTimeAndMember(theme, day, time, member);

        assertThat(exists).isTrue();
    }
}
