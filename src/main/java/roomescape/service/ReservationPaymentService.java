package roomescape.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.service.dto.BookedMemberResponse;
import roomescape.service.dto.BookedPaymentRequest;
import roomescape.service.dto.BookedPaymentResponse;
import roomescape.service.dto.UserBookedReservationResponse;
import roomescape.service.dto.PaymentApproveRequest;
import roomescape.service.dto.PaymentResponse;
import roomescape.service.dto.AdminReservationBookedResponse;
import roomescape.service.dto.ReservationConditionRequest;
import roomescape.service.dto.ReservationPaymentRequest;
import roomescape.service.dto.ReservationRequest;
import roomescape.service.dto.ReservationResponse;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.UserReservationResponse;
import roomescape.service.dto.WaitingRankResponse;

@Service
public class ReservationPaymentService {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationPaymentService(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    public ReservationResponse saveReservationWithPayment(ReservationPaymentRequest reservationPaymentRequest) {
        ReservationRequest reservationRequest = reservationPaymentRequest.toReservationRequest();
        ReservationResponse reservationResponse = reservationService.saveReservation(reservationRequest);

        if (reservationResponse.status() == ReservationStatus.BOOKED) {
            PaymentApproveRequest paymentApproveRequest = reservationPaymentRequest.toPaymentApproveRequest(
                    reservationResponse.reservationId());
            paymentService.requestApproval(paymentApproveRequest);
        }

        return reservationResponse;
    }

    @Transactional(readOnly = true)
    public List<UserReservationResponse> findMyAllReservationWithPayment(Long memberId, LocalDate date) {
        List<UserBookedReservationResponse> bookedResponses = reservationService.findBookedAfterDate(memberId, date);
        List<WaitingRankResponse> waitingRanks = reservationService.findWaitingRanksAfterDate(memberId, date);
        List<PaymentResponse> paymentResponses = paymentService.findPaidByMemberId(memberId);

        return Stream.concat(
                        UserReservationResponse.reservationsToResponseStream(bookedResponses, paymentResponses),
                        UserReservationResponse.waitingsToResponseStream(waitingRanks)
                )
                .sorted(Comparator.comparing(UserReservationResponse::dateTime))
                .toList();
    }

    public void cancelBookedAndRefund(Long bookedMemberId) {
        BookedMemberResponse bookedMemberResponse = reservationService.cancelBooked(bookedMemberId);

        Long reservationId = bookedMemberResponse.reservationId();
        Long memberId = bookedMemberResponse.memberId();

        paymentService.requestRefund(reservationId, memberId);
    }

    public void liquidateReservation(BookedPaymentRequest bookedPaymentRequest) {
        BookedMemberResponse bookedMemberResponse = reservationService.findBookedMember(bookedPaymentRequest.id());
        PaymentApproveRequest paymentApproveRequest = PaymentApproveRequest.of(bookedMemberResponse, bookedPaymentRequest);
        paymentService.requestApproval(paymentApproveRequest);
    }

    @Transactional(readOnly = true)
    public List<BookedPaymentResponse> findBookedPaymentByCondition(
            ReservationConditionRequest reservationConditionRequest) {
        List<AdminReservationBookedResponse> reservations = reservationService.findBookedReservationsByCondition(
                reservationConditionRequest);

        List<Long> reservationIds = reservations.stream()
                .map(AdminReservationBookedResponse::reservationId)
                .toList();

        List<Long> paidReservationIds = paymentService.findDoneStatusReservationIds(reservationIds);

        return reservations.stream()
                .map(reservation ->
                        BookedPaymentResponse.of(
                                reservation,
                                paidReservationIds.contains(reservation.reservationId())
                        )
                )
                .toList();
    }
}
