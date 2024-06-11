package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.IllegalRequestException;
import roomescape.member.service.MemberService;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MyReservationResponse;
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
    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;

    public ReservationService(MemberService memberService, ReservationTimeService reservationTimeService,
                              ThemeService themeService, ReservationRepository reservationRepository,
                              PaymentService paymentService) {
        this.memberService = memberService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
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
    public List<MyReservationResponse> findMyReservations(Long memberId) {
        return reservationRepository.findMyReservations(memberId).stream()
                .map(MyReservationResponse::new)
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
        ReservationRequest reservationRequest = new ReservationRequest(
                request.date(),
                memberId,
                request.timeId(),
                request.themeId()
        );
        Reservation saved = reservationRepository.save(createReservationFrom(reservationRequest));
        paymentService.confirmPayment(saved, request.extractPaymentInformation());

        return new ReservationResponse(saved);
    }

    @Transactional
    public ReservationResponse saveAdminReservation(ReservationRequest request) {
        Reservation saved = reservationRepository.save(createReservationFrom(request));
        return new ReservationResponse(saved);
    }

    private Reservation createReservationFrom(ReservationRequest request) {
        validateMemberReservationNotExistInSlot(request);

        return Reservation.createNewReservation(
                memberService.findById(request.memberId()),
                new ReservationDate(request.date()),
                reservationTimeService.findById(request.timeId()),
                themeService.findById(request.themeId())
        );
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
        reservationRepository.deleteById(id);
    }
}
