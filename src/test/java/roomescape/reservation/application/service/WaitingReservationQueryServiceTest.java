package roomescape.reservation.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.sign.password.Password;
import roomescape.common.domain.Email;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.domain.WaitingReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeDescription;
import roomescape.theme.domain.ThemeName;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.domain.ThemeThumbnail;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;
import roomescape.user.domain.User;
import roomescape.user.domain.UserName;
import roomescape.user.domain.UserRepository;
import roomescape.user.domain.UserRole;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WaitingReservationQueryServiceTest {

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @Autowired
    private WaitingReservationQueryService waitingReservationQueryService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("모든 예약 대기를 조회할 수 있다")
    void getAll() {
        // given
        ReservationTime reservationTime = createAndSaveReservationTime(LocalTime.of(10, 0));
        Theme theme = createAndSaveTheme("공포", "지구별 방탈출 최고");
        User user = createAndSaveUser();
        Long userId = user.getId();

        WaitingReservation waitingReservation = WaitingReservation.of(
                userId,
                1,
                ReservationDate.from(LocalDate.now().plusDays(1)),
                reservationTime,
                theme
        );
        waitingReservationRepository.save(waitingReservation);

        // when
        List<WaitingReservation> actual = waitingReservationQueryService.getAll();

        // then
        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("유저 Id가 같은 모든 예약 대기를 조회할 수 있다")
    void findUserIdById() {
        // given
        ReservationTime reservationTime = createAndSaveReservationTime(LocalTime.of(10, 0));
        Theme theme = createAndSaveTheme("공포", "지구별 방탈출 최고");
        User user = createAndSaveUser();
        Long userId = user.getId();

        WaitingReservation waitingReservation = WaitingReservation.of(
                userId,
                1,
                ReservationDate.from(LocalDate.now().plusDays(1)),
                reservationTime,
                theme
        );
        WaitingReservation save = waitingReservationRepository.save(waitingReservation);

        // when
        Long userIdById = waitingReservationQueryService.findUserIdById(save.getId());
        // then
        assertThat(userIdById).isEqualTo(userId);
    }

    // Helper 메서드들
    private ReservationTime createAndSaveReservationTime(LocalTime time) {
        return reservationTimeRepository.save(
                ReservationTime.from(time));
    }

    private Theme createAndSaveTheme(String name, String description) {
        return themeRepository.save(
                Theme.of(
                        ThemeName.from(name),
                        ThemeDescription.from(description),
                        ThemeThumbnail.from("www.making.com")));
    }

    private User createAndSaveUser() {
        return userRepository.save(
                User.of(
                        UserName.from("강산"),
                        Email.from("email@email.com"),
                        Password.fromEncoded("1234"),
                        UserRole.NORMAL));
    }
}
