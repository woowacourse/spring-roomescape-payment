package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.Member;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest
@Sql("/data/reservation.sql")
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("없는 시간에 예약 시도시 실패하는지 확인")
    void saveFailWhenTimeNotFound() {
        Member member = memberRepository.findById(1L).orElseThrow();
        Theme theme = themeRepository.findById(1L).orElseThrow();

        ReservationTime notSavedTime = new ReservationTime(999L, LocalTime.of(11, 56));
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(1), member.getId(), notSavedTime.getId(), theme.getId());

        assertThatThrownBy(() -> reservationService.save(reservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.NOT_FOUND_RESERVATION_TIME.getMessage());
    }

    @Test
    @DisplayName("없는 테마에 예약 시도시 실패하는지 확인")
    void saveFailWhenThemeNotFound() {
        Member member = memberRepository.findById(1L).orElseThrow();
        ReservationTime time = reservationTimeRepository.findById(1L).orElseThrow();

        Theme notSavedTheme = new Theme(999L, "theme", "description", "https://thumbnail.com");
        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(1), member.getId(), time.getId(), notSavedTheme.getId());

        assertThatThrownBy(() -> reservationService.save(reservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.NOT_FOUND_THEME.getMessage());
    }

    @Test
    @DisplayName("없는 회원 예약 시도시 실패하는지 확인")
    void saveFailWhenMemberNotFound() {
        ReservationTime time = reservationTimeRepository.findById(1L).orElseThrow();
        Theme theme = themeRepository.findById(1L).orElseThrow();

        Long notSavedMemberId = 999L;

        ReservationRequest reservationRequest = new ReservationRequest(
                LocalDate.now().plusDays(1), notSavedMemberId, time.getId(), theme.getId());

        assertThatThrownBy(() -> reservationService.save(reservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("첫 예약 시도시 status가 APPROVED 인지 확인")
    void saveFirstReservationApprove() {
        Member member = memberRepository.findById(1L).orElseThrow();
        ReservationTime time = reservationTimeRepository.findById(1L).orElseThrow();
        Theme theme = themeRepository.findById(1L).orElseThrow();
        LocalDate date = LocalDate.now().plusDays(1);

        ReservationRequest reservationRequest = new ReservationRequest(date, member.getId(), time.getId(),
                theme.getId());
        ReservationResponse reservationResponse = reservationService.save(reservationRequest);

        assertThat(reservationResponse.status()).isEqualTo(ReservationStatus.RESERVED_UNPAID);
    }

    @Test
    @DisplayName("동일한 회원이 중복 예약 시도시 실패하는지 확인")
    void saveFailDuplicateReservationBySameMember() {
        Member member = memberRepository.findById(1L).orElseThrow();
        ReservationTime time = reservationTimeRepository.findById(1L).orElseThrow();
        Theme theme = themeRepository.findById(1L).orElseThrow();
        LocalDate date = LocalDate.now().plusDays(1);

        ReservationRequest reservationRequest = new ReservationRequest(date, member.getId(), time.getId(), theme.getId());
        reservationService.save(reservationRequest);
        assertThatThrownBy(() -> reservationService.save(reservationRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.DUPLICATE_RESERVATION.getMessage());
    }

    @Test
    @DisplayName("서로 다른 회원이 동일한 날짜/시간/테마에 예약 시도시 status가 PENDING 인지 확인")
    void saveDuplicateReservationPending() {
        Member member1 = memberRepository.findById(1L).orElseThrow();
        Member member2 = memberRepository.findById(2L).orElseThrow();

        ReservationTime time = reservationTimeRepository.findById(1L).orElseThrow();
        Theme theme = themeRepository.findById(1L).orElseThrow();
        LocalDate date = LocalDate.now().plusDays(1);

        ReservationRequest reservationRequestByMember1 = new ReservationRequest(date, member1.getId(), time.getId(),
                theme.getId());
        ReservationRequest reservationRequestByMember2 = new ReservationRequest(date, member2.getId(), time.getId(),
                theme.getId());
        reservationService.save(reservationRequestByMember1);
        ReservationResponse reservationResponse = reservationService.save(reservationRequestByMember2);

        assertThat(reservationResponse.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("이미 지나간 시간에 예약 시도시 실패하는지 확인")
    void saveFailWhenPastTime() {
        Member member = memberRepository.findById(1L).orElseThrow();
        ReservationTime time = reservationTimeRepository.findById(1L).orElseThrow();
        Theme theme = themeRepository.findById(1L).orElseThrow();
        LocalDate pastDate = LocalDate.now().minusDays(1);

        ReservationRequest reservationRequestWithPastDate = new ReservationRequest(
                pastDate, member.getId(), time.getId(), theme.getId());

        assertThatThrownBy(() -> reservationService.save(reservationRequestWithPastDate))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.PAST_TIME_RESERVATION.getMessage());
    }
}
