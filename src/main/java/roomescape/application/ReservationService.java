package roomescape.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.application.dto.request.PaymentRequest;
import roomescape.application.dto.request.ReservationRequest;
import roomescape.application.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.WaitingRepository;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.ReservationTimeRepository;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.detail.ThemeRepository;
import roomescape.exception.BadRequestException;
import roomescape.infra.payment.PaymentClient;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentClient paymentClient;


    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            WaitingRepository waitingRepository,
            PaymentClient paymentClient
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public synchronized ReservationResponse addReservation(ReservationRequest request) {
        Reservation reservation = createReservation(
                request.currentDateTime(),
                request.date(),
                request.timeId(),
                request.themeId(),
                request.memberId(),
                request.paymentKey(),
                request.amount()
        );

        validateReservationExists(reservation);
        validateWaitingExists(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);

        paymentClient.confirmPayment(new PaymentRequest(request.paymentKey(), request.orderId(), request.amount()));

        return ReservationResponse.from(savedReservation);
    }

    public List<ReservationResponse> getReservationsByConditions(
            Long memberId,
            Long themeId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        List<Reservation> reservations = reservationRepository.findAllByConditions(memberId, themeId, dateFrom, dateTo);

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReservationById(Long id) {
        Reservation reservation = reservationRepository.getById(id);

        reservationRepository.delete(reservation);
    }

    private Reservation createReservation(
            LocalDateTime currentDateTime,
            LocalDate date,
            Long timeId,
            Long themeId,
            Long memberId
    ) {
        Member member = memberRepository.getById(memberId);
        ReservationTime reservationTime = reservationTimeRepository.getById(timeId);
        Theme theme = themeRepository.getById(themeId);

        ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);

        return Reservation.create(currentDateTime, detail, member);
    }

    private Reservation createReservation(
            LocalDateTime currentDateTime,
            LocalDate date,
            Long timeId,
            Long themeId,
            Long memberId,
            String paymentKey,
            Long amount
    ) {
        Member member = memberRepository.getById(memberId);
        ReservationTime reservationTime = reservationTimeRepository.getById(timeId);
        Theme theme = themeRepository.getById(themeId);

        ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);

        return Reservation.create(currentDateTime, detail, amount, paymentKey, member);
    }

    private void validateReservationExists(Reservation reservation) {
        boolean reservationExists = reservationRepository.existsByDetail(reservation.getDetail());

        if (reservationExists) {
            throw new BadRequestException("이미 예약이 존재합니다.");
        }
    }

    private void validateWaitingExists(Reservation reservation) {
        boolean waitingExists = waitingRepository.existsByDetail(reservation.getDetail());

        if (waitingExists) {
            throw new BadRequestException("예약 대기가 존재하여 예약을 할 수 없습니다.");
        }
    }
}
