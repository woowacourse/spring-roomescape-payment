package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.dto.BookedReservationReadOnly;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.domain.reservation.repository.WaitingMemberRepository;
import roomescape.service.dto.ReservationConditionRequest;

@Transactional
class ReservationRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WaitingMemberRepository waitingMemberRepository;

    @DisplayName("존재하는 예약 삭제")
    @Test
    void deleteExistById() {
        assertThatCode(() -> reservationRepository.deleteById(1L)).doesNotThrowAnyException();
    }

    @DisplayName("날짜와 테마로 예약된 시간을 찾는다.")
    @Test
    void findBookedTimesByDateAndTheme() {
        // given
        Theme theme = themeRepository.findById(2L).get();
        LocalDate date = LocalDate.parse("2024-05-30");

        // when
        List<ReservationTime> times = reservationRepository.findBookedTimesByDateAndTheme(date, theme);

        // then
        assertThat(times).hasSize(4).extracting("id", "startAt")
                .contains(
                        tuple(2L, LocalTime.parse("10:00")),
                        tuple(3L, LocalTime.parse("11:00")),
                        tuple(4L, LocalTime.parse("12:00")),
                        tuple(7L, LocalTime.parse("15:00"))
                );
    }

    @DisplayName("특정 기간 동안 예약이 많은 테마를 입력한 수 만큼 찾는다.")
    @Test
    void findPopularThemes() {
        // given
        LocalDate startDate = LocalDate.parse("2024-05-04");
        LocalDate endDate = LocalDate.parse("2024-05-30");
        Limit limit = Limit.of(2);

        // when
        List<Theme> themes = reservationRepository.findPopularThemes(startDate, endDate, limit);

        // then
        assertThat(themes).hasSize(2)
                .extracting("id", "name")
                .containsExactly(
                        tuple(2L, "이름2"),
                        tuple(1L, "이름1")
                );
    }

    @DisplayName("회원, 테마, 날짜 조건에 따라 동적으로 예약 내역을 조회할 수 있다.")
    @ParameterizedTest
    @MethodSource("provideFilterCondition")
    void findByConditions(ReservationConditionRequest condition, int resultSize) {
        // given // when
        List<BookedReservationReadOnly> reservations = reservationRepository.findByConditions(condition.dateFrom(),
                condition.dateTo(), condition.themeId(), condition.memberId());

        // then
        assertThat(reservations).hasSize(resultSize);
    }

    static Stream<Arguments> provideFilterCondition() {
        return Stream.of(
                Arguments.of(
                        new ReservationConditionRequest(null, null, null, null),
                        18
                ),
                Arguments.of(
                        new ReservationConditionRequest(1L, null, null, null),
                        4
                ),
                Arguments.of(
                        new ReservationConditionRequest(null, 1L, null, null),
                        4
                ),
                Arguments.of(
                        new ReservationConditionRequest(null, null, LocalDate.parse("2024-05-09"), null),
                        9
                ),Arguments.of(
                        new ReservationConditionRequest(null, null, null, LocalDate.parse("2024-05-09")),
                        11
                ),
                Arguments.of(
                        new ReservationConditionRequest(1L, 1L, null, null),
                        4
                ),
                Arguments.of(
                        new ReservationConditionRequest(2L, null, LocalDate.parse("2024-05-09"), null),
                        6
                ),
                Arguments.of(
                        new ReservationConditionRequest(1L, 1L, LocalDate.parse("2024-05-09"), LocalDate.parse("2024-05-30")),
                        1
                )
        );
    }
}

