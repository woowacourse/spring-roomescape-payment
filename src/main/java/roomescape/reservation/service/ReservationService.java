package roomescape.reservation.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.payment.domain.Payment;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.domain.*;
import roomescape.reservation.domain.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final MemberService memberService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final ReservationSlotService reservationSlotService;
    private final ReservationRepository reservationRepository;

    public ReservationService(MemberService memberService,
                              ReservationTimeService reservationTimeService,
                              ThemeService themeService,
                              ReservationSlotService reservationSlotService,
                              ReservationRepository reservationRepository) {
        this.memberService = memberService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.reservationSlotService = reservationSlotService;
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findReservationsByMemberId(Long memberId) {
        Member member = memberService.findMember(memberId);
        return reservationRepository.findAllByMember(member);
    }

    public List<Reservation> findReservations(Specification<Reservation> spec) {
        return reservationRepository.findAll(spec);
    }

    public List<Reservation> findReservations(ReservationStatus status) {
        return reservationRepository.findAllByStatus(status);
    }

    public Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BadRequestException("해당 ID에 대응되는 사용자 예약이 없습니다."));
    }

    public Reservation findReservation(LocalDate date, String themeName, LocalTime time, Long memberId) {
        return reservationRepository.findByDateAndTimeAndThemeNameAndMemberId(
                date, themeName, time, memberId);
    }

    public boolean hasSameReservation(LocalDate date, Long themeId, Long timeId) {
        return reservationRepository.existsByDateAndTimeIdAndThemeId(date, themeId, timeId);
    }

    public boolean hasSameReservation(ReservationSlot reservationSlot, Member member) {
        return reservationRepository.existsByReservationSlotAndMember(reservationSlot, member);
    }

    @Transactional
    public Reservation createReservationWithPayment(ReservationRequest reservationRequest, Long memberId, ReservationStatus reservationStatus, Payment payment) {
        LocalDate date = LocalDate.parse(reservationRequest.date());
        ReservationTime reservationTime = reservationTimeService.findReservationTime(reservationRequest.timeId());
        Theme theme = themeService.findTheme(reservationRequest.themeId());
        Member member = memberService.findMember(memberId);
        ReservationSlot reservationSlot = reservationSlotService.findReservationSlot(date, reservationTime, theme);

        validateReservation(reservationSlot, member);

        return reservationRepository.save(new Reservation(member, reservationSlot, reservationStatus, payment));
    }

    @Transactional
    public Reservation createReservation(ReservationRequest reservationRequest, Long memberId, ReservationStatus reservationStatus) {
        LocalDate date = LocalDate.parse(reservationRequest.date());
        ReservationTime reservationTime = reservationTimeService.findReservationTime(reservationRequest.timeId());
        Theme theme = themeService.findTheme(reservationRequest.themeId());
        Member member = memberService.findMember(memberId);
        ReservationSlot reservationSlot = reservationSlotService.findReservationSlot(date, reservationTime, theme);

        validateReservation(reservationSlot, member);

        return reservationRepository.save(new Reservation(member, reservationSlot, reservationStatus));
    }

    private void validateReservation(ReservationSlot reservationSlot, Member member) {
        if (reservationSlot.isPast()) {
            throw new BadRequestException("올바르지 않는 데이터 요청입니다.");
        }
        if (hasSameReservation(reservationSlot, member)) {
            throw new BadRequestException("중복된 예약입니다.");
        }
    }

    @Transactional
    public void updateWaitingOrder(ReservationSlot reservationSlot) {
        reservationRepository.findFirstByReservationSlotOrderByCreatedAt(reservationSlot)
                .ifPresent(Reservation::pendingReservation);
    }

    @Transactional
    public Reservation payReservation(LocalDate date, String themeName, LocalTime time, Long memberId, Payment payment) {
        Reservation reservation = findReservation(date, themeName, time, memberId);
        reservation.payReservation(payment);
        return reservation;
    }

    @Transactional
    public void deleteReservation(AuthInfo authInfo, Long reservationId) {
        Reservation reservation = findReservation(reservationId);
        Member member = memberService.findMember(authInfo.getId());
        if (member.isNotAdmin() && reservation.isNotBookedBy(member)) {
            throw new ForbiddenException("예약자가 아닙니다.");
        }
        deleteReservation(reservation.getId());
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void deleteReservationSlot(Long reservationSlotId) {
        reservationRepository.deleteByReservationSlot_Id(reservationSlotId);
        reservationSlotService.deleteById(reservationSlotId);
    }

    public int findMyWaitingOrder(Long id) {
        return reservationRepository.findMyWaitingOrder(id);
    }
}
