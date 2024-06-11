package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.BookedMemberRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.domain.reservation.repository.WaitingMemberRepository;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.LoginMember;
import roomescape.service.dto.ReservationRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.WaitingRankResponse;
import roomescape.service.dto.WaitingResponse;

@Transactional
class ReservationServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookedMemberRepository bookedMemberRepository;

    @Autowired
    private WaitingMemberRepository waitingMemberRepository;

    @DisplayName("예약 저장")
    @Test
    void saveReservation() {
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationRequest reservationRequest = new ReservationRequest(member.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId());
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationRequest);

        assertAll(
                () -> assertThat(reservationResponse.member().name()).isEqualTo("고구마"),
                () -> assertThat(reservationResponse.date()).isEqualTo(LocalDate.parse("2025-11-11")),
                () -> assertThat(reservationResponse.time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservationResponse.time().startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(reservationResponse.theme().id()).isEqualTo(theme.getId()),
                () -> assertThat(reservationResponse.theme().name()).isEqualTo(theme.getName()),
                () -> assertThat(reservationResponse.theme().description()).isEqualTo(theme.getDescription()),
                () -> assertThat(reservationResponse.theme().thumbnail()).isEqualTo(theme.getThumbnail())
        );
    }

    @DisplayName("예약이 이미 존재하면 예약 대기 상태가 된다.")
    @Test
    void saveWaitReservation() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member1 = memberRepository.save(Member.createUser("고구마1", "email1@email.com", "1234"));
        Member member2 = memberRepository.save(Member.createUser("고구마2", "email2@email.com", "1234"));

        ReservationRequest reservationRequest1 = new ReservationRequest(member1.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId());
        ReservationRequest reservationRequest2 = new ReservationRequest(member2.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId());

        ReservationResponse reservationResponse1 = reservationService.saveReservation(reservationRequest1);

        // when
        ReservationResponse reservationResponse2 = reservationService.saveReservation(reservationRequest2);

        // then
        assertAll(
                () -> assertThat(reservationResponse2.member().name()).isEqualTo("고구마2"),
                () -> assertThat(reservationResponse2.date()).isEqualTo(LocalDate.parse("2025-11-11")),
                () -> assertThat(reservationResponse2.time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservationResponse2.time().startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(reservationResponse2.theme().id()).isEqualTo(theme.getId()),
                () -> assertThat(reservationResponse2.theme().name()).isEqualTo(theme.getName()),
                () -> assertThat(reservationResponse2.theme().description()).isEqualTo(theme.getDescription()),
                () -> assertThat(reservationResponse2.theme().thumbnail()).isEqualTo(theme.getThumbnail()),
                () -> assertThat(reservationResponse2.status()).isEqualTo(ReservationStatus.WAIT)
        );
    }

    @DisplayName("존재하지 않는 예약 시간으로 예약 저장")
    @Test
    void timeForSaveReservationNotFound() {
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationRequest reservationRequest = new ReservationRequest(member.getId(),
                LocalDate.parse("2025-11-11"), 100L, 1L);
        assertThatThrownBy(() -> {
            reservationService.saveReservation(reservationRequest);
        }).isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("어드민은 예약을 삭제한다.")
    @Test
    void deleteByAdminAdminReservationByAdmin() {
        int size = bookedMemberRepository.findAll().size();
        reservationService.cancelBooked(1L);
        assertThat(bookedMemberRepository.findAll()).hasSize(size - 1);
    }

    @DisplayName("유저는 예약 대기를 삭제한다.")
    @Test
    void deleteByAdminUser() {
        int size = waitingMemberRepository.findAll().size();

        reservationService.cancelWaiting(1L, new LoginMember(3L, "유저3", Role.USER));
        assertThat(waitingMemberRepository.findAll()).hasSize(size - 1);
    }

    @DisplayName("존재하지 않는 예약 삭제")
    @Test
    void deleteByAdminNotFound() {
        assertThatThrownBy(() -> {
            reservationService.cancelBooked(100L);
        }).isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("한 사람이 중복된 예약을 할 수 없다.")
    @Test
    void saveDuplicatedReservation() {
        ReservationRequest reservationRequest = new ReservationRequest(1L,
                LocalDate.parse("2024-05-04"),
                1L, 1L);

        assertThatThrownBy(() -> reservationService.saveReservation(reservationRequest))
                .isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("내 예약 대기를 조회하면 예약 대기 순번도 함께 표시한다.")
    @Test
    void findAllMyReservations() {
        // given // when
        List<WaitingRankResponse> waitingRanks = reservationService.findWaitingRanksAfterDate(1L,
                LocalDate.parse("2024-05-30"));

        // then
        assertThat(waitingRanks).hasSize(2)
                .extracting("id", "rank")
                .containsExactly(
                        tuple(2L, 2L),
                        tuple(3L, 1L)
                );
    }

    @DisplayName("예약 대기 목록을 조회한다.")
    @Test
    void findAllWaiting() {
        // given // when
        List<WaitingResponse> allWaiting = reservationService.findAllWaiting();

        // then
        assertThat(allWaiting).hasSize(3)
                .extracting("name", "theme", "date", "startAt")
                .containsExactlyInAnyOrder(
                        tuple("유저2", "이름2", LocalDate.parse("2024-05-30"), LocalTime.parse("10:00")),
                        tuple("어드민", "이름2", LocalDate.parse("2024-05-30"), LocalTime.parse("10:00")),
                        tuple("어드민", "이름2", LocalDate.parse("2024-05-30"), LocalTime.parse("11:00"))
                );
    }
}
