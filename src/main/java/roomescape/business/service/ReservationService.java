package roomescape.business.service;

import static roomescape.exception.ErrorCode.RESERVATION_DUPLICATED;
import static roomescape.exception.ErrorCode.RESERVATION_NOT_EXIST;
import static roomescape.exception.ErrorCode.THEME_NOT_EXIST;
import static roomescape.exception.ErrorCode.USER_NOT_EXIST;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Member;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.entity.Waiting;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.ReservationDate;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.infrastructure.WaitingRepository;
import roomescape.presentation.dto.request.AdminReservationRequest;
import roomescape.presentation.dto.request.ReservationCondition;
import roomescape.presentation.dto.request.ReservationRequest;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.ReservationWithPaymentResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final PaymentService paymentService;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final WaitingRepository waitingRepository;

    public ReservationResponse addAndGet(LoginInfo loginInfo, ReservationRequest request) {
        Member member = memberRepository.findById(Id.create(loginInfo.id()))
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        TimeSlot timeSlot = reservationTimeRepository.findById(Id.create(request.timeId()))
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
        Theme theme = themeRepository.findById(Id.create(request.themeId()))
                .orElseThrow(() -> new NotFoundException(THEME_NOT_EXIST));

        validateDuplicatedReservation(request.date(), timeSlot, theme);
        Reservation reservation = reservationRepository.save(
                Reservation.create(member, request.date(), timeSlot, theme));
        paymentService.pay(reservation, request.paymentKey(), request.orderId(), request.amount());
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse addAndGetWithoutPayment(AdminReservationRequest request) {
        Member member = memberRepository.findById(Id.create(request.userId()))
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        TimeSlot timeSlot = reservationTimeRepository.findById(Id.create(request.timeId()))
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
        Theme theme = themeRepository.findById(Id.create(request.themeId()))
                .orElseThrow(() -> new NotFoundException(THEME_NOT_EXIST));

        validateDuplicatedReservation(request.date(), timeSlot, theme);
        Reservation reservation = Reservation.create(member, request.date(), timeSlot, theme);
        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    private void validateDuplicatedReservation(LocalDate date, TimeSlot timeSlot, Theme theme) {
        if (reservationRepository.existsByDate_ValueAndTimeSlot_StartAtAndThemeId(date, timeSlot.getStartAt(),
                theme.getId())) {
            throw new DuplicatedException(RESERVATION_DUPLICATED);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllReservations(ReservationCondition condition) {
        return reservationRepository.findAllReservationWithFilter(
                        Id.create(condition.themeId()), Id.create(condition.userId()), condition.dateFrom(), condition.dateTo()
                )
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void cancelReservationAndPromoteWait(String id) {
        Reservation reservation = reservationRepository.findById(Id.create(id))
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
        reservationRepository.delete(reservation);
        TimeSlot time = reservation.getTimeSlot();
        ReservationDate date = reservation.getDate();
        waitingRepository.findFirstByDateAndTimeAndThemeOrderById(date, time, reservation.getTheme())
                .ifPresent(this::promoteWaitingToReservation);
    }

    private void promoteWaitingToReservation(Waiting waiting) {
        Reservation newReservation = waiting.convertToReservation();
        reservationRepository.save(newReservation);
        waitingRepository.delete(waiting);
    }

    @Transactional(readOnly = true)
    public List<ReservationWithPaymentResponse> getMyReservations(final String userIdValue) {
        Id userId = Id.create(userIdValue);
        return reservationRepository.findByMemberIdWithPayment(userId.id());
    }
}
