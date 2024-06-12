package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.IllegalRequestException;
import roomescape.member.service.MemberService;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.payment.service.PaymentClient;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.WaitingResponse;
import roomescape.theme.service.ThemeService;
import roomescape.time.service.ReservationTimeService;

@Service
public class ReservationService {

    private final MemberService memberService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public ReservationService(MemberService memberService, ReservationTimeService reservationTimeService,
                              ThemeService themeService, ReservationRepository reservationRepository,
                              PaymentRepository paymentRepository,
                              PaymentClient paymentClient) {
        this.memberService = memberService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllReservation() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByMemberAndThemeAndPeriod(Long memberId, Long themeId, LocalDate dateFrom,
                                                                      LocalDate dateTo) {
        return reservationRepository.findByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo).stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberReservationResponse> findMemberReservationWithInformation(Long memberId) {
        return reservationRepository.findByMemberIdWithInformation(memberId).stream()
                .map(MemberReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WaitingResponse> findReservationsOnWaiting() {
        return reservationRepository.findReservationOnWaiting().stream()
                .map(WaitingResponse::new)
                .toList();
    }


    @Transactional
    public ReservationResponse saveMemberReservation(Long memberId, MemberReservationAddRequest request) {
        paymentClient.requestConfirmPayment(request.extractPaymentInformation());
        ReservationRequest reservationRequest = new ReservationRequest(
                request.date(),
                memberId,
                request.timeId(),
                request.themeId()
        );
        ReservationResponse reservationResponse = saveReservation(reservationRequest);
        savePayment(reservationResponse, request);
        return reservationResponse;
    }

    private void savePayment(ReservationResponse reservationResponse, MemberReservationAddRequest request) {
        Reservation reservation = reservationRepository.findById(reservationResponse.id())
                .orElseThrow(() -> new IllegalRequestException("예약 번호가 존재하지 않습니다: " + reservationResponse.id()));
        paymentRepository.save(new Payment(reservation, request.paymentKey(), request.amount()));
    }

    @Transactional
    public ReservationResponse saveReservation(ReservationRequest request) {
        validateMemberReservationNotExistInSlot(request);

        Reservation newReservation = Reservation.createNewReservation(
                memberService.findById(request.memberId()),
                new ReservationDate(request.date()),
                reservationTimeService.findById(request.timeId()),
                themeService.findById(request.themeId())
        );

        Reservation saved = reservationRepository.save(newReservation);
        return new ReservationResponse(saved);
    }

    private void validateMemberReservationNotExistInSlot(ReservationRequest request) {
        Reservations sameSlotReservations = new Reservations(reservationRepository.findByDateAndTimeAndTheme(
                request.date(),
                request.timeId(),
                request.themeId()
        ));

        if (sameSlotReservations.hasReservationMadeBy(request.memberId())) {
            throw new IllegalRequestException("해당 아이디로 진행되고 있는 예약(대기)이 이미 존재합니다");
        }
    }

    @Transactional
    public void removeReservation(long id) {
        Optional<Payment> payment = paymentRepository.findByReservationId(id);
        if (payment.isPresent()) {
            paymentClient.cancelPayment(payment.get().getPaymentKey());
            paymentRepository.deleteById(payment.get().getId());
        }
        reservationRepository.deleteById(id);
    }
}
