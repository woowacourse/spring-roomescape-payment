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
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingWithRank;
import roomescape.reservation.exception.WaitingNotFoundException;
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
class WaitingServiceTest {

    private static final LocalDate futureDate = TestFixture.makeFutureDate();

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    private WaitingService waitingService;
    private ReservationTime time;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setUp() {
        waitingService = new WaitingService(waitingRepository);

        ReservationTime time2 = ReservationTime.withUnassignedId(LocalTime.of(9, 0));
        time = reservationTimeRepository.save(time2);
        theme = themeRepository.save(TestFixture.makeTheme(1L));
        member = memberRepository.save(TestFixture.makeMember());
    }

    @Test
    void findWaitings_shouldReturnAllWaitings() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);
        waitingRepository.save(waiting);

        List<ReservationResponse> result = waitingService.findWaitings();
        assertThat(result).hasSize(1);
    }

    @Test
    void findWaitings_shouldReturnEmptyList_whenNoWaitings() {
        List<ReservationResponse> result = waitingService.findWaitings();
        assertThat(result).isEmpty();
    }

    @Test
    void findMyWaitingsWithRank_shouldReturnMemberWaitingsWithRank() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);
        waitingRepository.save(waiting);

        List<WaitingWithRank> result = waitingService.findMyWaitingsWithRank(
                new UserInfo(member.getId(), MemberRole.USER));
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getRank()).isEqualTo(1)
        );
    }

    @Test
    void findMyWaitingsWithRank_shouldReturnEmptyList_whenNoWaitings() {
        List<WaitingWithRank> result = waitingService.findMyWaitingsWithRank(
                new UserInfo(member.getId(), MemberRole.USER));
        assertThat(result).isEmpty();
    }

    @Test
    void findMaxOrderByDateAndTimeAndTheme_shouldReturnHighestTurn() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting1 = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);
        Waiting waiting2 = Waiting.createUpcomingReservationWithUnassignedId(member, 2, info);
        waitingRepository.save(waiting1);
        waitingRepository.save(waiting2);

        int maxTurn = waitingService.findMaxOrderByDateAndTimeAndTheme(futureDate, time.getId(), theme.getId());
        assertThat(maxTurn).isEqualTo(2);
    }

    @Test
    void findMaxOrderByDateAndTimeAndTheme_shouldReturnZero_whenNoWaitings() {
        int maxTurn = waitingService.findMaxOrderByDateAndTimeAndTheme(futureDate, time.getId(), theme.getId());
        assertThat(maxTurn).isZero();
    }

    @Test
    void save_shouldCreateNewWaiting() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);

        Waiting saved = waitingService.save(waiting);
        assertAll(
                () -> assertThat(saved).isNotNull(),
                () -> assertThat(saved.getTurn()).isEqualTo(1),
                () -> assertThat(saved.getDate()).isEqualTo(futureDate),
                () -> assertThat(saved.getTime().getStartAt()).isEqualTo(LocalTime.of(9, 0))
        );
    }

    @Test
    void findFirstWaitingOfInfo_shouldReturnFirstWaiting() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting1 = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);
        Waiting waiting2 = Waiting.createUpcomingReservationWithUnassignedId(member, 2, info);
        waitingRepository.save(waiting1);
        waitingRepository.save(waiting2);

        Waiting first = waitingService.findFirstWaitingOfInfo(info);
        assertAll(
                () -> assertThat(first).isNotNull(),
                () -> assertThat(first.getTurn()).isEqualTo(1),
                () -> assertThat(first.getDate()).isEqualTo(futureDate)
        );
    }

    @Test
    void findFirstWaitingOfInfo_shouldThrowException_whenNoWaitingExists() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);

        assertThatThrownBy(() -> waitingService.findFirstWaitingOfInfo(info))
                .isInstanceOf(WaitingNotFoundException.class)
                .hasMessageContaining("요청한 id와 일치하는 대기 정보가 없습니다.");
    }

    @Test
    void isWaitingExists_shouldReturnTrue_whenWaitingExists() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);
        waitingRepository.save(waiting);

        boolean exists = waitingService.isWaitingExists(info);
        assertThat(exists).isTrue();
    }

    @Test
    void isWaitingExists_shouldReturnFalse_whenNoWaitingExists() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);

        boolean exists = waitingService.isWaitingExists(info);
        assertThat(exists).isFalse();
    }

    @Test
    void delete_shouldRemoveWaiting() {
        ReservationInfo info = new ReservationInfo(futureDate, time, theme);
        Waiting waiting = Waiting.createUpcomingReservationWithUnassignedId(member, 1, info);
        waiting = waitingRepository.save(waiting);

        waitingService.delete(waiting.getId());
        assertThat(waitingRepository.findAll()).isEmpty();
    }
} 