package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.global.auth.dto.UserInfo;
import roomescape.payment.infrastructure.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.exception.ReservationNotFoundException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.dto.ReservationWithPayment;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(final ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<ReservationResponse> findReservations(final Long themeId, final Long memberId,
                                                      final LocalDate startDate,
                                                      final LocalDate endDate) {
        return getReservations(themeId, memberId, startDate, endDate)
                .stream()
                .map(ReservationResponse::of)
                .toList();
    }

    public List<Reservation> getReservations(final Long themeId, final Long memberId,
                                             final LocalDate startDate, final LocalDate endDate) {
        if ((themeId == null) || (memberId == null) || (startDate == null) || (endDate == null)) {
            return reservationRepository.findAll();
        }
        return reservationRepository.findByInfoThemeIdAndMemberIdAndInfoDateBetween(themeId, memberId, startDate,
                endDate);
    }

    public void delete(Long id) {
        paymentRepository.deleteById(id);
        reservationRepository.deleteById(id);
    }

    public boolean isReservationExists(ReservationRequest request) {
        return reservationRepository.existsByInfoDateAndInfoTimeIdAndInfoThemeId(request.date(), request.timeId(),
                request.themeId());
    }

    public List<ReservationWithPayment> findMyReservations(final UserInfo userInfo) {
        return reservationRepository.findReservationWithPaymentByMemberId(userInfo.id());
    }

    public Reservation save(final Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation findById(final Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("요청한 id와 일치하는 예약 정보가 없습니다."));
    }
}

