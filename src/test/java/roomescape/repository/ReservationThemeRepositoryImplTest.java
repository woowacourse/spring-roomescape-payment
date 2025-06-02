package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationThemeRepository;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.test_util.RepositoryTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationThemeRepositoryImplTest extends RepositoryTest {

    @Autowired
    private ReservationThemeRepository themeRepository;

    @DisplayName("id로 테마 데이터를 성공적으로 가져온다.")
    @Test
    void findById() {
        // given
        ReservationTheme savedTheme = insertReservationTheme("테마", "설명", "썸네일");

        // when
        final Optional<ReservationTheme> theme = themeRepository.findById(savedTheme.getId());

        // then
        assertAll(
                () -> assertThat(theme).isPresent(),
                () -> assertThat(theme.get().getId()).isEqualTo(savedTheme.getId()),
                () -> assertThat(theme.get().getName()).isEqualTo(savedTheme.getName()),
                () -> assertThat(theme.get().getDescription()).isEqualTo(savedTheme.getDescription()),
                () -> assertThat(theme.get().getThumbnail()).isEqualTo(savedTheme.getThumbnail())
        );
    }

    @DisplayName("모든 테마 데이터를 성공적으로 가져온다.")
    @Test
    void findAll() {
        // given
        ReservationTheme savedTheme1 = insertReservationTheme("테마1", "설명", "썸네일");
        ReservationTheme savedTheme2 = insertReservationTheme("테마2", "설명", "썸네일");
        ReservationTheme savedTheme3 = insertReservationTheme("테마3", "설명", "썸네일");

        // when
        final List<ReservationTheme> themes = themeRepository.findAll();

        // then
        assertAll(
                () -> assertThat(themes).hasSize(3),
                () -> assertThat(themes.get(0).getName()).isEqualTo(savedTheme1.getName()),
                () -> assertThat(themes.get(1).getName()).isEqualTo(savedTheme2.getName()),
                () -> assertThat(themes.get(2).getName()).isEqualTo(savedTheme3.getName())
        );
    }

    @DisplayName("주간 인기테마를 성공적으로 가져온다.")
    @Test
    void findWeeklyThemeOrderByCountDesc() {
        // given
        Member member = insertMember("email@email.com", "password", "돔푸", MemberRole.USER);
        ReservationTheme theme1 = insertReservationTheme("테마1", "설명", "썸네일");
        ReservationTheme theme2 = insertReservationTheme("테마2", "설명", "썸네일");
        ReservationTheme theme3 = insertReservationTheme("테마3", "설명", "썸네일");
        ReservationTime time1 = insertReservationTime(LocalTime.now().plusHours(1));
        ReservationTime time2 = insertReservationTime(LocalTime.now().plusHours(2));
        ReservationTime time3 = insertReservationTime(LocalTime.now().plusHours(3));

        ReservationItem item1 = insertReservationItem(LocalDate.now().plusDays(1), time1, theme1);
        ReservationItem item2 = insertReservationItem(LocalDate.now().plusDays(1), time2, theme1);
        ReservationItem item3 = insertReservationItem(LocalDate.now().plusDays(1), time3, theme1);

        ReservationItem item4 = insertReservationItem(LocalDate.now().plusDays(2), time1, theme2);
        ReservationItem item5 = insertReservationItem(LocalDate.now().plusDays(2), time2, theme2);

        ReservationItem item6 = insertReservationItem(LocalDate.now().plusDays(3), time1, theme3);
        ReservationItem item7 = insertReservationItem(LocalDate.now().plusDays(3), time2, theme3);
        ReservationItem item8 = insertReservationItem(LocalDate.now().plusDays(3), time3, theme3);

        insertReservation(member, item1, ReservationStatus.ACCEPTED);
        insertReservation(member, item2, ReservationStatus.ACCEPTED);
        insertReservation(member, item3, ReservationStatus.ACCEPTED);

        insertReservation(member, item4, ReservationStatus.ACCEPTED);
        insertReservation(member, item5, ReservationStatus.ACCEPTED);

        insertReservation(member, item6, ReservationStatus.ACCEPTED);
        insertReservation(member, item7, ReservationStatus.ACCEPTED);
        insertReservation(member, item8, ReservationStatus.ACCEPTED);

        // when
        final List<ReservationTheme> weeklyThemeOrderByCountDesc = themeRepository.findWeeklyThemeOrderByCountDesc(
                2,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );

        // then
        assertAll(
                () -> assertThat(weeklyThemeOrderByCountDesc).hasSize(2),
                () -> assertThat(weeklyThemeOrderByCountDesc.stream()
                        .map(ReservationTheme::getName)
                        .toList())
                        .containsExactly(theme1.getName(), theme2.getName())
        );
    }

    @DisplayName("테마를 성공적으로 저장한다.")
    @Test
    void save() {
        // given
        final ReservationTheme newTheme = new ReservationTheme("new Theme", "new Description", "new Thumbnail");

        // when
        final ReservationTheme saved = themeRepository.save(newTheme);

        // then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(newTheme.getName()),
                () -> assertThat(saved.getDescription()).isEqualTo(newTheme.getDescription()),
                () -> assertThat(saved.getThumbnail()).isEqualTo(newTheme.getThumbnail())
        );
    }

    @DisplayName("id로 테마를 성공적으로 삭제한다.")
    @Test
    void deleteById() {
        // given
        ReservationTheme savedTheme = insertReservationTheme("테마1", "설명", "썸네일");

        // when, then
        assertThatCode(() -> themeRepository.deleteById(savedTheme.getId()))
                .doesNotThrowAnyException();
    }

    @DisplayName("이미 존재하는 테마이므로 true를 반환한다.")
    @Test
    void existsByName() {
        // given
        ReservationTheme savedTheme = insertReservationTheme("테마1", "설명", "썸네일");

        // when
        final boolean expected = themeRepository.existsByName(savedTheme.getName());

        // then
        assertThat(expected).isTrue();
    }
}
