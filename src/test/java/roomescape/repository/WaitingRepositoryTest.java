package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.domain.Member;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.business.WaitingWithRank;

@DataJpaTest
class WaitingRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private WaitingRepository waitingRepository;

    private ReservationTime time;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setup() {
        time = entityManager.persist(
                ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        theme = entityManager.persist(
                Theme.createWithoutId("테마", "테마 설명", "thumbnail.jpg"));
        member = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "일반회원", "commonmember@test.com", "qwer1234!"));

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("회원의 예약 대기 목록을 순번과 함께 구할 수 있다.")
    @Test
    void findWithRankingByMember() {
        // given
        Member otherMember = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원2", "member2@test.com", "qwer1234!"));

        entityManager.flush();
        entityManager.clear();

        jdbcTemplate.update(String.format("""
                INSERT INTO waiting(date, theme_id, time_id, member_id, created_at, updated_at)
                VALUES ('2025-05-10', %d, %d, %d, '2025-05-23 20:37:43.488281', '2025-05-23 20:37:43.488281')
                """, theme.getId(), time.getId(), otherMember.getId()));
        jdbcTemplate.update(String.format("""
                INSERT INTO waiting(date, theme_id, time_id, member_id, created_at, updated_at)
                VALUES ('2025-05-10', %d, %d, %d, '2025-05-23 23:37:43.488281', '2025-05-23 20:37:43.488281')
                """, theme.getId(), time.getId(), member.getId()));

        // when
        List<WaitingWithRank> waitingWithRankings = waitingRepository.findWithRankingByMember(member.getId());

        // then
        assertAll(
                () -> assertThat(waitingWithRankings).hasSize(1),
                () -> assertThat(waitingWithRankings)
                        .extracting(WaitingWithRank::getRank)
                        .containsExactly(2L)
        );
    }

    @Nested
    @DisplayName("중복된 대기 데이터 존재여부를 확인할 수 있다.")
    public class existsDuplicated {

        @DisplayName("중복이 아닌 경우 false를 리턴한다.")
        @Test
        void isNotDuplicate() {
            // when
            boolean isDuplicated = waitingRepository.existsDuplicated(
                    theme.getId(), NEXT_DAY, time.getId(), member.getId());

            // then
            assertThat(isDuplicated).isFalse();
        }

        @DisplayName("중복인 경우 true를 리턴한다.")
        @Test
        void isDuplicate() {
            // given
            entityManager.persist(Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, member));

            entityManager.flush();
            entityManager.clear();

            // when
            boolean isDuplicated = waitingRepository.existsDuplicated(
                    theme.getId(), NEXT_DAY, time.getId(), member.getId());

            // then
            assertThat(isDuplicated).isTrue();

        }
    }

    @DisplayName("특정 테마의 날짜시간에 대한 첫번째 예약 대기를 조회할 수 있다.")
    @Test
    void findFirstWaiting() {
        // given
        Member otherMember = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member1@test.com", "qwer1234!"));
        Waiting firstWaiting = entityManager.persist(
                Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, member));
        entityManager.persist(
                Waiting.createWithoutIdWithoutPayment(NEXT_DAY, theme, time, otherMember));

        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Waiting> findWaiting = waitingRepository.findFirstWaiting(theme.getId(), NEXT_DAY, time.getId());

        // then
        assertAll(
                () -> assertThat(findWaiting).isPresent(),
                () -> assertThat(findWaiting.get()).isEqualTo(firstWaiting)
        );
    }
}
