package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.PaymentResponse;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.slot.*;
import roomescape.exception.PaymentException;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

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
    private ReservationRepository reservationRepository;

    @DisplayName("예약 저장")
    @Test
    void saveReservation() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(member.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");

        // when
        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(1000, "orderId", "paymentKey");
        PaymentResponse paymentResponse = new PaymentResponse(
                "mId",
                "paymentKey",
                "orderId",
                PaymentStatus.DONE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                1000);
        doReturn(paymentResponse)
                .when(paymentClient)
                .confirmPayment(paymentRequest);

        ReservationResponse reservationResponse = reservationService.saveReservation(reservationPaymentRequest);

        // then
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

    @DisplayName("결제 오류시 예약이 저장되지 않는다.")
    @Test
    void saveReservationPaymentError() {
        // given
        LocalDate date = LocalDate.parse("2025-11-11");
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member = memberRepository.save(Member.createUser("고구마", "email@email.com", "1234"));

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(member.getId(),
                date, time.getId(), theme.getId(), 1000, "orderId", "paymentKey");

        // when
        PaymentConfirmRequest paymentConfirmRequest = reservationPaymentRequest.toPaymentRequest();
        doThrow(PaymentException.class)
                .when(paymentClient)
                .confirmPayment(paymentConfirmRequest);

        // then
        assertAll(
                () -> assertThatThrownBy(() -> reservationService.saveReservation(reservationPaymentRequest))
                        .isInstanceOf(PaymentException.class),
                () -> assertThat(reservationRepository.findBySlot(new ReservationSlot(date, time, theme))).isEmpty()
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

        ReservationPaymentRequest reservationPaymentRequest1 = new ReservationPaymentRequest(member1.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");
        ReservationPaymentRequest reservationPaymentRequest2 = new ReservationPaymentRequest(member2.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");

        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(1000, "orderId", "paymentKey");
        PaymentResponse paymentResponse = new PaymentResponse(
                "mId",
                "paymentKey",
                "orderId",
                PaymentStatus.DONE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                1000);
        doReturn(paymentResponse)
                .when(paymentClient)
                .confirmPayment(paymentRequest);

        ReservationResponse reservationResponse1 = reservationService.saveReservation(reservationPaymentRequest1);

        // when
        ReservationResponse reservationResponse2 = reservationService.saveReservation(reservationPaymentRequest2);

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

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                member.getId(), LocalDate.parse("2025-11-11"), 100L, 1L,
                1000, "orderId", "paymentKey");
        assertThatThrownBy(() -> {
            reservationService.saveReservation(reservationPaymentRequest);
        }).isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("어드민은 예약을 삭제한다.")
    @Test
    void deleteByAdminAdminReservationByAdmin() {
        int size = reservationRepository.findAll().size();
        reservationService.cancelReservation(1L);
        assertThat(reservationRepository.findAll()).hasSize(size - 1);
    }

    @DisplayName("유저는 예약 대기를 삭제한다.")
    @Test
    void deleteByAdminUser() {
        int size = reservationRepository.findAll().size();

        reservationService.cancelReservation(18L);
        assertThat(reservationRepository.findAll()).hasSize(size - 1);
    }

    @DisplayName("존재하지 않는 예약 삭제")
    @Test
    void deleteByAdminNotFound() {
        assertThatThrownBy(() -> {
            reservationService.cancelReservation(100L);
        }).isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("한 사람이 중복된 예약을 할 수 없다.")
    @Test
    void saveDuplicatedReservation() {
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                1L, LocalDate.parse("2024-06-04"), 1L, 1L,
                1000, "orderId", "paymentKey");

        assertThatThrownBy(() -> reservationService.saveReservation(reservationPaymentRequest))
                .isInstanceOf(RoomEscapeBusinessException.class);
    }

    @DisplayName("내 예약을 조회하면 예약 대기 순번도 함께 표시한다.")
    @Test
    void findAllMyReservations() {
        // given // when
        List<UserReservationResponse> allUserReservation = reservationService.findMyAllReservationAndWaiting(1L, LocalDate.parse("2024-06-30"));

        // then
        assertThat(allUserReservation).hasSize(3)
                .extracting("id", "status", "rank")
                .containsExactly(
                        tuple(14L, "예약", 0L),
                        tuple(2L, "예약대기", 2L),
                        tuple(3L, "예약대기", 1L)
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
                        tuple("유저2", "이름2", LocalDate.parse("2024-06-30"), LocalTime.parse("10:00")),
                        tuple("어드민", "이름2", LocalDate.parse("2024-06-30"), LocalTime.parse("10:00")),
                        tuple("어드민", "이름2", LocalDate.parse("2024-06-30"), LocalTime.parse("11:00"))
                );
    }

    @DisplayName("예약을 삭제했을 때 자동으로 첫번째 예약 대기자가 예약된다.")
    @Test
    void cancelReservation() {
        // given
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.parse("01:00")));
        Theme theme = themeRepository.save(new Theme("이름", "설명", "썸네일"));
        Member member1 = memberRepository.save(Member.createUser("고구마1", "email1@email.com", "1234"));
        Member member3 = memberRepository.save(Member.createUser("고구마3", "email3@email.com", "1234"));
        Member member2 = memberRepository.save(Member.createUser("고구마2", "email2@email.com", "1234"));

        ReservationPaymentRequest reservationPaymentRequest1 = new ReservationPaymentRequest(member1.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");
        ReservationPaymentRequest reservationPaymentRequest2 = new ReservationPaymentRequest(member2.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");
        ReservationPaymentRequest reservationPaymentRequest3 = new ReservationPaymentRequest(member3.getId(),
                LocalDate.parse("2025-11-11"), time.getId(), theme.getId(), 1000, "orderId", "paymentKey");

        PaymentConfirmRequest paymentRequest = new PaymentConfirmRequest(1000, "orderId", "paymentKey");
        PaymentResponse paymentResponse = new PaymentResponse(
                "mId",
                "paymentKey",
                "orderId",
                PaymentStatus.DONE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                1000);
        doReturn(paymentResponse)
                .when(paymentClient)
                .confirmPayment(paymentRequest);

        Long reservationId = reservationService.saveReservation(reservationPaymentRequest1).id();
        reservationService.saveReservation(reservationPaymentRequest2);
        reservationService.saveReservation(reservationPaymentRequest3);

        // when
        reservationService.cancelReservation(reservationId);

        // then
        assertThat(reservationRepository.findById(reservationId).get().getMember()).isEqualTo(member2);
    }
}
