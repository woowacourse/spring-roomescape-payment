package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static roomescape.fixture.DateFixture.getNextDay;
import static roomescape.fixture.MemberFixture.getMemberChoco;
import static roomescape.fixture.MemberFixture.getMemberClover;
import static roomescape.fixture.ReservationFixture.getNextDayReservation;
import static roomescape.fixture.ReservationTimeFixture.getNoon;
import static roomescape.fixture.ThemeFixture.getTheme1;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.domain.MemberReservation;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.service.dto.MemberReservationCreate;
import roomescape.reservation.service.dto.WaitingCreate;
import roomescape.util.ServiceTest;

@DisplayName("예약 서비스 로직 테스트")
class ReservationApplicationServiceTest extends ServiceTest {

    ReservationTime time;
    Theme theme1;
    Member memberChoco;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationApplicationService reservationApplicationService;

    @BeforeEach
    void setUp() {
        time = reservationTimeRepository.save(getNoon());
        theme1 = themeRepository.save(getTheme1());
        memberChoco = memberRepository.save(getMemberChoco());
        String paymentType = "카드";
        BigDecimal totalAmount = BigDecimal.valueOf(15000L);
        ResponseEntity<PaymentResponse> okResponse = ResponseEntity.ok(
                new PaymentResponse("paymentKey", "DONE", "MC4wOTA5NzEwMjg3MjQ2", totalAmount, paymentType));
        doReturn(okResponse).when(paymentClient).confirm(any(), anyString());
    }


    @DisplayName("중복 예약 대기 시 예외가 발생한다.")
    @Test
    void duplicatedWaitingList() {
        //given
        Reservation reservation = reservationRepository.save(getNextDayReservation(time, theme1));
        memberReservationRepository.save(new MemberReservation(memberChoco, reservation, ReservationStatus.APPROVED));

        //when & then
        assertThatThrownBy(() -> reservationApplicationService.addWaiting(
                new WaitingCreate(memberChoco.getId(), reservation.getDate(), reservation.getTime().getId(),
                        reservation.getTheme().getId())))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorType.DUPLICATED_RESERVATION_ERROR.getMessage());
    }

    @DisplayName("대기한 예약 취소에 성공한다.")
    @Test
    void deleteWaiting() {
        //given
        Member memberClover = memberRepository.save(getMemberClover());
        LocalDate date = getNextDay();
        ReservationResponse waitingResponse = reservationApplicationService.addWaiting(
                new WaitingCreate(memberClover.getId(), date, time.getId(), theme1.getId())
        );

        //when
        AuthInfo authInfo = new AuthInfo(memberClover.getId(), memberClover.getName(), memberClover.getEmail(),
                memberClover.getRole());
        reservationApplicationService.deleteMemberReservation(authInfo, waitingResponse.memberReservationId());

        //then
        assertThat(memberReservationRepository.findByMemberId(memberClover.getId())).hasSize(0);
    }

    @DisplayName("대기 예약이 아닌 예약 삭제 시, 예외가 발생한다.")
    @Test
    void deleteNotWaitingReservation() {
        //given
        LocalDate date = getNextDay();
        ReservationResponse reservationResponse = reservationApplicationService.createMemberReservation(
                new MemberReservationCreate(memberChoco.getId(), theme1.getId(), time.getId(), "paymentKey", "orderId",
                        BigDecimal.valueOf(10000L), date)
        );

        //when & then
        assertThatThrownBy(() -> reservationApplicationService.deleteWaiting(AuthInfo.from(memberChoco),
                reservationResponse.memberReservationId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorType.NOT_A_WAITING_RESERVATION.getMessage());
    }

    @DisplayName("일자와 시간 중복 시 예외가 발생한다.")
    @Test
    void duplicatedReservation() {
        //given
        Reservation reservation = reservationRepository.save(getNextDayReservation(time, theme1));
        memberReservationRepository.save(new MemberReservation(memberChoco, reservation, ReservationStatus.APPROVED));
        LocalDate date = getNextDay();

        //when & then
        assertThatThrownBy(() -> reservationApplicationService.createMemberReservation(
                new MemberReservationCreate(memberChoco.getId(), theme1.getId(), time.getId(), "paymentKey", "orderId",
                        BigDecimal.valueOf(10000L), date)
        )).isInstanceOf(
                BadRequestException.class).hasMessage(ErrorType.DUPLICATED_RESERVATION_ERROR.getMessage());
    }

    @DisplayName("기존 예약이 삭제 될 경우, 대기하는 다음 예약이 자동으로 승인된다.")
    @Test
    void changeToApprove() {
        //given
        Member memberClover = memberRepository.save(getMemberClover());

        Reservation reservation = reservationRepository.save(getNextDayReservation(time, theme1));
        MemberReservation firstReservation = memberReservationRepository.save(
                new MemberReservation(memberChoco, reservation, ReservationStatus.APPROVED));
        MemberReservation waitingReservation = memberReservationRepository.save(
                new MemberReservation(memberClover, reservation, ReservationStatus.PENDING));

        entityManager.clear();
        entityManager.flush();

        //when
        reservationApplicationService.deleteMemberReservation(AuthInfo.from(memberChoco), firstReservation.getId());

        //then
        Optional<MemberReservation> optionalMemberReservation = memberReservationRepository
                .findById(waitingReservation.getId());
        assertAll(
                () -> assertThat(optionalMemberReservation).isNotNull(),
                () -> assertThat(optionalMemberReservation.get()
                        .getReservationStatus()).isEqualTo(ReservationStatus.NOT_PAID)
        );
    }

    @DisplayName("예약 삭제 시, 환불된다.")
    @Test
    void deleteRefund() {
        //given
        Reservation reservation = reservationRepository.save(getNextDayReservation(time, theme1));
        MemberReservation memberReservation = memberReservationRepository.save(
                new MemberReservation(memberChoco, reservation, ReservationStatus.APPROVED));

        //when
        reservationApplicationService.delete(reservation.getId());

        //then
        assertThat(paymentRepository.findByMemberReservationId(memberReservation.getId())).isEmpty();
    }
}
