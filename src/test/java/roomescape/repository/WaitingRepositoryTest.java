package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.model.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class WaitingRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WaitingRepository waitingRepository;

    @DisplayName("예약 대기를 추가한다")
    @Test
    void should_add_waiting() {
        LocalDate day = LocalDate.now().plusDays(2);
        ReservationTime time = reservationTimeRepository.findById(1L).get();
        Theme theme = themeRepository.findById(1L).get();
        Member member = memberRepository.findById(1L).get();

        waitingRepository.save(new Waiting(day, time, theme, member));

        assertThat(waitingRepository.count()).isEqualTo(3);
    }

    @DisplayName("예약 대기를 삭제한다")
    @Test
    void should_delete_waiting() {
        waitingRepository.deleteById(2L);

        assertThat(waitingRepository.count()).isEqualTo(1);
    }

    @DisplayName("아이디에 해당하는 예약 대기가 존재하면 참을 반환한다.")
    @Test
    void should_return_true_when_id_exist() {
        boolean exists = waitingRepository.existsById(1L);

        assertThat(exists).isTrue();
    }

    @DisplayName("사용자 아이디에 해당하는 예약 대기를 반환한다.")
    @Test
    void should_return_member_waiting() {
        Member member = memberRepository.findById(2L).get();

        List<WaitingWithRank> waiting = waitingRepository.findWaitingWithRankByMemberId(member.getId());
        assertThat(waiting).hasSize(2);
    }

    @DisplayName("날짜, 시간, 테마, 멤버에 해당하는 예약 대기가 존재하면 참을 반환한다.")
    @Test
    void should_return_true_when_date_and_time_and_theme_and_member_exist() {
        LocalDate day = LocalDate.now().plusDays(1);
        ReservationTime time = reservationTimeRepository.findById(1L).get();
        Theme theme = themeRepository.findById(1L).get();
        Member member = memberRepository.findById(2L).get();

        boolean exists = waitingRepository.existsWaitingByThemeAndDateAndTimeAndMember(theme, day, time, member);
        assertThat(exists).isTrue();
    }

    @DisplayName("모든 예약 대기를 조회한다.")
    @Test
    void should_get_all_waiting() {
        List<Waiting> reservations = waitingRepository.findAll();

        assertThat(reservations).hasSize(2);
    }

    @DisplayName("주어진 조건에 맞는 예약 대기를 조회한다.")
    @Test
    void should_return_waiting_when_given_theme_and_date_and_time_and_member() {
        LocalDate day = LocalDate.now().plusDays(1);
        ReservationTime time = reservationTimeRepository.findById(1L).get();
        Theme theme = themeRepository.findById(1L).get();


        Waiting waiting = waitingRepository.findFirstByThemeAndDateAndTime(theme, day, time).get();
        assertThat(waiting.getId()).isEqualTo(1L);
    }
}
