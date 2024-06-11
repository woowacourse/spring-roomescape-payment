package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.controller.dto.UserReservationSaveRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.repository.MemberRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ReservationTimeRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationSlot;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.exception.customexception.business.RoomEscapeBusinessException;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.dto.request.PaymentApproveRequest;
import roomescape.service.dto.request.ReservationSaveRequest;
import roomescape.service.dto.response.PaymentApproveResponse;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.service.dto.response.UserReservationResponse;
import roomescape.service.reservation.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static roomescape.domain.reservation.ReservationStatus.RESERVED;

@Transactional
@ExtendWith(MockitoExtension.class)
@EnableRetry
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
    private ReservationRepository reservationRepository;

    @DisplayName("예약 저장")
    @Test
    void saveReservation() {
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(member.getId(),
                LocalDate.parse("2025-11-11"),
                time.getId(), theme.getId());
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationSaveRequest);

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

    @DisplayName("존재하지 않는 예약 시간으로 예약 저장")
    @Test
    void timeForSaveReservationNotFound() {
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(member.getId(),
                LocalDate.parse("2025-11-11"), 100L, 1L);
        assertThatThrownBy(() -> {
            reservationService.saveReservation(reservationSaveRequest);
        }).isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("대기가 있는 예약 삭제 요청 시 대기가 자동 승인된다")
    @Test
    void should_AcceptWaiting_When_ReservationHasWaiting() {
        int size = reservationRepository.findAll().size();
        reservationService.deleteReservation(1L);
        assertThat(reservationRepository.findAll()).hasSize(size);
    }

    @DisplayName("대기가 없는 예약 삭제 요청시, 예약이 삭제된다")
    @Test
    void should_DeleteReservation_When_ReservationHasNotWaiting() {
        int size = reservationRepository.findAll().size();
        reservationService.deleteReservation(4L);
        assertThat(reservationRepository.findAll()).hasSize(size - 1);
    }

    @DisplayName("존재하지 않는 예약 삭제")
    @Test
    void deleteReservationNotFound() {
        assertThatThrownBy(() -> {
            reservationService.deleteReservation(100L);
        }).isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("중복된 예약 저장")
    @Test
    void saveDuplicatedReservation() {
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(1L, LocalDate.parse("2024-05-04"),
                1L, 1L);
        assertThatThrownBy(() -> reservationService.saveReservation(reservationSaveRequest))
                .isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("내 예약을 조회한다.")
    @Test
    void findAllMyReservations() {
        // given
        long memberId = 1L;
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(memberId, LocalDate.now().plusDays(1L), 1L, 1L);
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationSaveRequest);

        // when
        List<UserReservationResponse> allUserReservation = reservationService.findAllUserReservation(memberId);

        // then
        ReservationSlot slot = allUserReservation.get(0).reservationSlot();
        assertAll(
                () -> assertThat(allUserReservation).hasSize(1),
                () -> assertThat(slot.getDate()).isEqualTo(reservationSaveRequest.date()),
                () -> assertThat(slot.getTime().getStartAt()).isEqualTo(reservationResponse.time().startAt()),
                () -> assertThat(slot.getTheme().getName()).isEqualTo(reservationResponse.theme().name()),
                () -> assertThat(allUserReservation.get(0).status()).isEqualTo(RESERVED)
        );
    }

    @DisplayName("성공 : 예약 성공 - 결제 성공")
    @Test
    void successReservation() {
        //given
        LoginMember member = new LoginMember(2L, "user1", Role.USER);
        UserReservationSaveRequest request = new UserReservationSaveRequest(
                LocalDate.now(),
                3L,
                3L,
                "testPaymentKey",
                "testOrderId",
                "1000",
                "NORMAL"
        );
        PaymentApproveResponse response = new PaymentApproveResponse(request.paymentKey(), request.orderId());
        Mockito.when(paymentService.pay(any(PaymentApproveRequest.class), any(Reservation.class))).thenReturn(response);

        //when
        ReservationResponse reservationResponse = reservationService.saveUserReservation(member, request);

        //then
        assertAll(
                () -> Mockito.verify(paymentService, times(1)).pay(any(PaymentApproveRequest.class), any(Reservation.class)),
                () -> assertThat(reservationResponse.date()).isEqualTo(request.date()),
                () -> assertThat(reservationResponse.theme().id()).isEqualTo(request.themeId()),
                () -> assertThat(reservationResponse.time().id()).isEqualTo(request.timeId()),
                () -> assertThat(reservationResponse.member().id()).isEqualTo(member.id())
        );
    }

    @DisplayName("실패 : 예약 성공 - 결제 실패")
    @Test
    void failReservation_When_FailPay() {
        //given
        LoginMember member = new LoginMember(2L, "user1", Role.USER);
        UserReservationSaveRequest request = new UserReservationSaveRequest(
                LocalDate.now(),
                3L,
                3L,
                "testPaymentKey",
                "testOrderId",
                "1000",
                "NORMAL"
        );

        Mockito.when(paymentService.pay(any(PaymentApproveRequest.class), any(Reservation.class)))
                .thenThrow(RoomEscapeBusinessException.class);

        //when - then
        assertAll(
                () -> assertThatThrownBy(() -> reservationService.saveUserReservation(member, request))
                        .isInstanceOf(RoomEscapeBusinessException.class),
                () -> Mockito.verify(paymentService, times(1)).pay(any(PaymentApproveRequest.class), any(Reservation.class))
        );
    }
}
