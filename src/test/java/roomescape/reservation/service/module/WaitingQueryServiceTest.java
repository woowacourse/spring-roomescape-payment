package roomescape.reservation.service.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.MEMBER_KAKI;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TOMORROW;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.util.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingWithRank;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.repository.WaitingRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class WaitingQueryServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private WaitingQueryService waitingQueryService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("대기와 대기 번호를 조회한다.")
    @Test
    void findWaitingWithRanksByMemberId() {
        Member jojo = memberRepository.save(MEMBER_JOJO);
        Member kaki = memberRepository.save(MEMBER_KAKI);
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);

        waitingRepository.save(new Waiting(jojo, TOMORROW, theme, reservationTime));
        Waiting secondWaiting = waitingRepository.save(new Waiting(kaki, TOMORROW, theme, reservationTime));

        List<WaitingWithRank> waitingWithRanks = waitingQueryService.findWaitingWithRanksByMemberId(kaki.getId());

        assertAll(
                () -> assertThat(waitingWithRanks.get(0).getRank()).isEqualTo(2L),
                () -> assertThat(waitingWithRanks.get(0).getWaiting().getId()).isEqualTo(secondWaiting.getId())
        );
    }
}
