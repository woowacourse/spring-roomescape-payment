package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.MyReservationResponse;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.repository.ReservationRepository;

import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationResponse create(final Reservation reservation) {
        validate(reservation);
        final Reservation saved = reservationRepository.save(reservation);
        return ReservationResponse.from(saved);
    }

    private void validate(final Reservation reservation) {
        if (!reservation.isAvailable()) {
            throw new IllegalArgumentException("이전 날짜 혹은 당일은 예약할 수 없습니다.");
        }

        final boolean isReserved = reservationRepository.existsByDateAndTime_IdAndTheme_Id(
                reservation.getDate(), reservation.getReservationTimeId(), reservation.getThemeId());
        if (isReserved) {
            throw new IllegalArgumentException("해당 시간대에 예약이 모두 찼습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        final List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBy(final ReservationFilterParam filterParam) {
        final List<Reservation> reservations = reservationRepository.findByTheme_IdAndMember_IdAndDateBetween(
                filterParam.themeId(), filterParam.memberId(), filterParam.dateFrom(), filterParam.dateTo()
        );
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse delete(final Long id) {
        final Reservation reservation = findReservation(id);
        reservationRepository.deleteById(id);
        return ReservationResponse.from(reservation);
    }

    public List<MyReservationResponse> findMyReservations(final Long id) {
        final List<Reservation> reservations = reservationRepository.findByMember_Id(id);
        return reservations.stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    @Transactional
    public MyReservationResponse updatePayment(Long id, PaymentResponse paymentResponse) {
        final Reservation reservation = findReservation(id);
        reservation.updatePaymentKey(paymentResponse.paymentKey());
        return MyReservationResponse.from(reservation);
    }

    public void checkMyReservation(Long id, LoginMember loginMember) {
        Reservation reservation = findReservation(id);
        if (reservation.isNotReservedBy(loginMember.id())) {
            throw new IllegalArgumentException("예약자가 일치하지 않습니다.");
        }
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 예약이 있습니다."));
    }
}
