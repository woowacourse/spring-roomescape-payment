package roomescape.registration.domain.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.admin.domain.FilterInfo;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.admin.dto.ReservationFilterRequest;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.MemberExceptionCode;
import roomescape.exception.model.ReservationExceptionCode;
import roomescape.exception.model.ReservationTimeExceptionCode;
import roomescape.exception.model.ThemeExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.dto.ReservationTimeAvailabilityResponse;
import roomescape.registration.domain.reservation.repository.ReservationRepository;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.repository.WaitingRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
                              MemberRepository memberRepository, WaitingRepository waitingRepository, PaymentService paymentService, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationResponse addReservation(ReservationRequest reservationRequest, long id) {
        ReservationTime time = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new RoomEscapeException(ReservationTimeExceptionCode.FOUND_TIME_IS_NULL_EXCEPTION));
        Theme theme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new RoomEscapeException(ThemeExceptionCode.FOUND_THEME_IS_NULL_EXCEPTION));
        Member member = memberRepository.findMemberById(id)
                .orElseThrow(() -> new RoomEscapeException(ThemeExceptionCode.FOUND_MEMBER_IS_NULL_EXCEPTION));
        validateDateAndTimeWhenSave(reservationRequest.date(), time);

        Reservation saveReservation = new Reservation(reservationRequest.date(), time, theme, member);
        Reservation reservation = reservationRepository.save(saveReservation);

        paymentService.sendConfirmRequestAndSavePayment(reservationRequest, reservation);

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public void addAdminReservation(AdminReservationRequest adminReservationRequest) {
        ReservationTime time = reservationTimeRepository.findById(adminReservationRequest.timeId())
                .orElseThrow(() -> new RoomEscapeException(ReservationTimeExceptionCode.FOUND_TIME_IS_NULL_EXCEPTION));
        Theme theme = themeRepository.findById(adminReservationRequest.themeId())
                .orElseThrow(() -> new RoomEscapeException(ThemeExceptionCode.FOUND_THEME_IS_NULL_EXCEPTION));
        Member member = memberRepository.findMemberById(adminReservationRequest.memberId())
                .orElseThrow(() -> new RoomEscapeException(MemberExceptionCode.MEMBER_NOT_EXIST_EXCEPTION));

        validateDateAndTimeWhenSave(adminReservationRequest.date(), time);
        Reservation saveReservation = new Reservation(adminReservationRequest.date(), time, theme, member);
        ReservationResponse.from(reservationRepository.save(saveReservation));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findReservations() {
        List<Reservation> reservations = reservationRepository.findAllByOrderByDateAscReservationTimeAsc();

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeAvailabilityResponse> findTimeAvailability(long themeId, LocalDate date) {
        List<ReservationTime> allTimes = reservationTimeRepository.findAllByOrderByStartAt();
        List<Reservation> reservations = reservationRepository.findAllByThemeIdAndDate(themeId, date);
        List<ReservationTime> bookedTimes = extractReservationTimes(reservations);

        return allTimes.stream()
                .map(time -> ReservationTimeAvailabilityResponse.fromTime(time, isTimeBooked(time, bookedTimes)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findFilteredReservations(ReservationFilterRequest reservationFilterRequest) {
        FilterInfo filterInfo = reservationFilterRequest.toFilterInfo();

        return reservationRepository.findAllByMemberIdAndThemeIdAndDateBetween(filterInfo.getMemberId(),
                        filterInfo.getThemeId(), filterInfo.getFromDate(), filterInfo.getToDate()).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findMemberReservations(long id) {
        List<Reservation> reservations = reservationRepository.findAllByMemberId(id);
        return reservations.stream()
                .map(reservation -> {
                    Optional<Payment> optionalPayment = paymentRepository.findByReservationId(reservation.getId());
                    if (optionalPayment.isPresent()) {
                        return ReservationResponse.from(reservation, optionalPayment.get());
                    }

                    return ReservationResponse.from(reservation);
                })
                .toList();
    }

    @Transactional
    public void removeReservation(long reservationId) {
        if (paymentRepository.existsByReservationId(reservationId)) {
            throw new RoomEscapeException(ReservationExceptionCode.RESERVATION_ALREADY_PAID_CAN_NOT_DELETE_EXCEPTION);
        }

        Optional<Waiting> waiting = waitingRepository.findFirstByReservationIdOrderByCreatedAt(reservationId);

        if (waiting.isEmpty()) {
            reservationRepository.deleteById(reservationId);
            return;
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomEscapeException(ReservationExceptionCode.RESERVATION_NOT_EXIST));
        reservation.setMember(waiting.get().getReservation().getMember());

        reservationRepository.save(reservation);
        waitingRepository.deleteById(waiting.get().getId());
    }

    private List<ReservationTime> extractReservationTimes(List<Reservation> reservations) {
        return reservations.stream()
                .map(Reservation::getReservationTime)
                .toList();
    }

    private boolean isTimeBooked(ReservationTime time, List<ReservationTime> bookedTimes) {
        return bookedTimes.contains(time);
    }

    private void validateDateAndTimeWhenSave(LocalDate date, ReservationTime time) {
        if (date.isBefore(LocalDate.now())) {
            throw new RoomEscapeException(ReservationExceptionCode.RESERVATION_DATE_IS_PAST_EXCEPTION);
        }

        if (date.equals(LocalDate.now())) {
            validateTime(time);
        }
    }

    private void validateTime(ReservationTime time) {
        if (time.isBeforeTime(LocalTime.now())) {
            throw new RoomEscapeException(ReservationExceptionCode.RESERVATION_TIME_IS_PAST_EXCEPTION);
        }
    }
}
