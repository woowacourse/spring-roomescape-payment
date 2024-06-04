package roomescape.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationWaiting;
import roomescape.domain.payment.Payment;
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.dto.LoginMemberWaitingResponse;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationWaitingRepository;
import roomescape.service.mapper.LoginMemberReservationResponseMapper;
import roomescape.service.mapper.LoginMemberWaitingResponseMapper;
import roomescape.service.mapper.ReservationPaymentMapper;
import roomescape.service.mapper.ReservationWaitingResponseMapper;

@Service
public class MyReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public MyReservationService(ReservationRepository reservationRepository,
                                ReservationWaitingRepository waitingRepository,
                                PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<LoginMemberReservationResponse> findLoginMemberReservations(long memberId) {
        List<Reservation> reservations = reservationRepository.findByMemberId(memberId);
        List<Payment> payments = paymentRepository.findAllByReservationIn(reservations);
        Map<Reservation, Payment> reservationPaymentMap = ReservationPaymentMapper.toMap(reservations, payments);

        return reservationPaymentMap.entrySet().stream()
                .map(entry -> LoginMemberReservationResponseMapper.toResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<LoginMemberWaitingResponse> findByMemberIdFromWaiting(long memberId) {
        List<ReservationWaiting> allByMemberId = waitingRepository.findAllByMemberId(memberId);
        return allByMemberId.stream()
                .map(waiting -> {
                    int priority = waiting.calculatePriority(allByMemberId);
                    return ReservationWaitingResponseMapper.toResponse(waiting, priority);
                })
                .map(LoginMemberWaitingResponseMapper::from)
                .toList();
    }
}
