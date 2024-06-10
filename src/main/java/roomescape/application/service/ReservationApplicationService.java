package roomescape.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.member.domain.LoginMember;
import roomescape.payment.api.PaymentClient;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.dto.ReservationDetailResponse;
import roomescape.reservation.dto.ReservationPaymentDetail;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationPaymentResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@Service
public class ReservationApplicationService {
    private final PaymentClient paymentClient;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;

    public ReservationApplicationService(PaymentClient paymentClient, ReservationService reservationService, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.reservationService = reservationService;
        this.paymentRepository = paymentRepository;
    }

    public ReservationPaymentResponse reservationPayment(LoginMember loginMember, ReservationPaymentRequest reservationPaymentRequest) {
        PaymentInfo paymentInfo = paymentClient.payment(reservationPaymentRequest.toPaymentRequest());
        ReservationResponse reservationResponse = reservationService.save(
                loginMember,
                reservationPaymentRequest.toReservationRequest(),
                paymentInfo
        );
        return new ReservationPaymentResponse(reservationResponse, PaymentResponse.from(paymentInfo));
    }

    @Transactional
    public List<ReservationPaymentDetail> reservationPaymentDetails(long memberId) {
        List<ReservationDetailResponse> allByMemberId = reservationService.findAllByMemberId(memberId);
        List<ReservationPaymentDetail> paymentDetails = new ArrayList<>();
        for (ReservationDetailResponse reservationDetailResponse : allByMemberId) {
            paymentRepository.findByReservationId(reservationDetailResponse.reservationId())
                    .ifPresent(payment -> paymentDetails.add(
                            new ReservationPaymentDetail(reservationDetailResponse, PaymentResponse.from(payment)))
                    );
        }
        return paymentDetails;
    }
}
