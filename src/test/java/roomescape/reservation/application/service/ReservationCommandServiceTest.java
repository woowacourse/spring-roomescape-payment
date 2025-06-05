package roomescape.reservation.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.sign.password.Password;
import roomescape.common.domain.Email;
import roomescape.common.exception.DuplicateException;
import roomescape.common.exception.NotFoundException;
import roomescape.common.time.TimeProvider;
import roomescape.reservation.application.dto.CreateReservationServiceRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.WaitingReservationRepository;
import roomescape.reservation.exception.PastDateReservationException;
import roomescape.reservation.exception.PastTimeReservationException;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ReservationCommandServiceTest {

    @Autowired
    private ReservationCommandService reservationCommandService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private WaitingReservationRepository waitingReservationRepository;

    @Test
    @DisplayName("예약을 생성할 수 있다")
    void createAndFindReservation() {
        // given
        ReservationTime reservationTime = createAndSaveReservationTime(LocalTime.now().plusMinutes(2));
        Theme theme = createAndSaveTheme();
        User user = createAndSaveUser();

        CreateReservationServiceRequest requestDto = createReservationRequest(
                user.getId(),
                LocalDate.now().plusDays(1),
                reservationTime.getId(),
                theme.getId());

        // when
        Reservation reservation = reservationCommandService.create(requestDto);

        // then
        Reservation found = reservationRepository.findById(reservation.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(reservation).isEqualTo(found);
        assertThat(reservation.getId()).isEqualTo(found.getId());
        assertThat(reservation.getUserId()).isEqualTo(found.getUserId());
        assertThat(reservation.getDate()).isEqualTo(found.getDate());
        assertThat(reservation.getTime()).isEqualTo(found.getTime());
        assertThat(reservation.getTheme()).isEqualTo(found.getTheme());
    }

    @Test
    @DisplayName("중복된 예약을 생성할 수 없다.")
    void existsReservation() {
        // given
        ReservationTime reservationTime = createAndSaveReservationTime(LocalTime.now().plusMinutes(2));
        Theme theme = createAndSaveTheme();
        User user = createAndSaveUser();

        CreateReservationServiceRequest requestDto = createReservationRequest(
                user.getId(),
                LocalDate.now().plusDays(1),
                reservationTime.getId(),
                theme.getId());

        Reservation savedReservation = reservationCommandService.create(requestDto);

        // when
        // then
        assertThatThrownBy(() -> reservationCommandService.create(requestDto))
                .isInstanceOf(DuplicateException.class)
                .hasMessageContainingAll(
                        "RESERVATION already exists.",
                        "params={ReservationDate=ReservationDate(value=",
                        "Long=",
                        "Long="
                );
    }

    @Test
    @DisplayName("예약을 삭제할 수 있다")
    void deleteReservation() {
        // given
        ReservationTime reservationTime = createAndSaveReservationTime(LocalTime.now().plusMinutes(5));
        Theme theme = createAndSaveTheme();
        User user = createAndSaveUser();

        Reservation reservation = reservationRepository.save(
                Reservation.of(
                        user.getId(),
                        ReservationDate.from(LocalDate.now().plusDays(1)),
                        reservationTime,
                        theme));

        // when
        reservationCommandService.delete(reservation.getId());

        // then
        assertThat(reservationRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("지나간 날짜/시간에 대한 예약을 생성할 수 없다")
    void cannotCreatePastDateTimeReservation() {
        // given
        LocalDateTime now = timeProvider.now();

        ReservationTime validReservationTime = createAndSaveReservationTime(
                now.toLocalTime().plusMinutes(2));
        ReservationTime pastReservationTime = createAndSaveReservationTime(
                now.toLocalTime().minusMinutes(2));

        User user = createAndSaveUser();
        Theme theme = createAndSaveTheme();

        CreateReservationServiceRequest pastDateReservationRequest = createReservationRequest(
                user.getId(),
                now.toLocalDate().minusDays(1),
                validReservationTime.getId(),
                theme.getId());

        CreateReservationServiceRequest pastTimeReservationRequest = createReservationRequest(
                user.getId(),
                now.toLocalDate(),
                pastReservationTime.getId(),
                theme.getId());

        // when & then
        assertAll(() -> {
            assertThatThrownBy(() -> reservationCommandService.create(pastDateReservationRequest))
                    .isInstanceOf(PastDateReservationException.class)
                    .hasMessageContaining("Attempted to reserve with past date.");

            assertThatThrownBy(() -> reservationCommandService.create(pastTimeReservationRequest))
                    .isInstanceOf(PastTimeReservationException.class)
                    .hasMessageContaining("Attempted to reserve with past time.");
        });
    }

    @Test
    @DisplayName("존재하지 않는 예약을 삭제하려 하면 예외가 발생한다")
    void deleteNonExistentReservation() {
        // given
        Long id = -1L;

        // when & then
        assertThatThrownBy(() -> reservationCommandService.delete(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[RESERVATION] not found. params={Long=-1}");
    }

    private ReservationTime createAndSaveReservationTime(LocalTime time) {
        return reservationTimeRepository.save(
                ReservationTime.from(time));
    }

    private Theme createAndSaveTheme() {
        return themeRepository.save(
                Theme.of(
                        ThemeName.from("공포"),
                        ThemeDescription.from("지구별 방탈출 최고"),
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

    private CreateReservationServiceRequest createReservationRequest(Long userId, LocalDate date, Long timeId, Long themeId) {
        return new CreateReservationServiceRequest(
                userId,
                ReservationDate.from(date),
                timeId,
                themeId);
    }
}
