package roomescape.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Member;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Status;
import roomescape.core.domain.Theme;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;
import roomescape.core.dto.reservation.AdminReservationRequest;
import roomescape.core.dto.reservation.MemberReservationRequest;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.core.repository.ReservationTimeRepository;
import roomescape.core.repository.ThemeRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;

    public ReservationService(final ReservationRepository reservationRepository,
                              final ReservationTimeRepository reservationTimeRepository,
                              final ThemeRepository themeRepository,
                              final MemberRepository memberRepository,
                              final PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse create(final MemberReservationRequest memberReservationRequest,
                                      final LoginMember loginMember) {
        final Reservation reservationToSave = new Reservation(
                getMember(loginMember.getId()),
                memberReservationRequest.getDate(),
                getReservationTime(memberReservationRequest.getTimeId()),
                getTheme(memberReservationRequest.getThemeId()),
                Status.findStatus(memberReservationRequest.getStatus()),
                LocalDateTime.now()
        );
        validateDuplicateReservation(reservationToSave);
        reservationToSave.validateDateAndTime();

        Reservation reservation = reservationRepository.save(reservationToSave);
        paymentService.approvePayment(reservation, new PaymentRequest(memberReservationRequest));

        return new ReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse create(final AdminReservationRequest adminReservationRequest) {
        final Reservation reservation = new Reservation(
                getMember(adminReservationRequest.getMemberId()),
                adminReservationRequest.getDate(),
                getReservationTime(adminReservationRequest.getTimeId()),
                getTheme(adminReservationRequest.getThemeId()),
                Status.findStatus(adminReservationRequest.getStatus()),
                LocalDateTime.now());

        validateDuplicateReservation(reservation);
        reservation.validateDateAndTime();

        return new ReservationResponse(reservationRepository.save(reservation));
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);
    }

    private ReservationTime getReservationTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(IllegalArgumentException::new);
    }

    private Theme getTheme(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateDuplicateReservation(final Reservation reservation) {
        int count = 0;
        if (reservation.isBooked()) {
            count = reservationRepository.countByDateAndTimeAndTheme(
                    reservation.getDate(), reservation.getReservationTime(), reservation.getTheme());
        }
        if (reservation.isStandBy()) {
            count = reservationRepository.countByMemberAndDateAndTimeAndTheme(
                    reservation.getMember(), reservation.getDate(), reservation.getReservationTime(),
                    reservation.getTheme()
            );
        }
        if (count > 0) {
            throw new IllegalArgumentException("해당 시간에 이미 예약 내역이 존재합니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAllByStatus(Status.BOOKED)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional
    public List<MyReservationResponse> findAllByMember(final LoginMember loginMember) {
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(IllegalArgumentException::new);
        return reservationRepository.findAllByMember(member)
                .stream()
                .map(this::getMyReservationResponse)
                .toList();
    }

    private MyReservationResponse getMyReservationResponse(final Reservation reservation) {
        return paymentService.findByReservation(reservation)
                .map(paymentResponse -> getMyReservationResponseWithPayment(reservation, paymentResponse))
                .orElseGet(() -> getReservationResponseWithoutPayment(reservation));
    }

    private MyReservationResponse getMyReservationResponseWithPayment(final Reservation reservation,
                                                                      final PaymentResponse paymentResponse) {
        if (reservation.isBooked()) {
            return MyReservationResponse.ofReservation(reservation, paymentResponse.getPaymentKey(),
                    paymentResponse.getAmount());
        }
        return MyReservationResponse.ofReservationWaiting(reservation, findRankByCreateAt(reservation),
                paymentResponse.getPaymentKey(), paymentResponse.getAmount());
    }

    private Integer findRankByCreateAt(final Reservation reservation) {
        return reservationRepository.countByCreateAtRank(reservation.getDate(), reservation.getReservationTime(),
                reservation.getTheme(), reservation.getCreateAt());
    }

    private MyReservationResponse getReservationResponseWithoutPayment(Reservation reservation) {
        if (reservation.isBooked()) {
            return MyReservationResponse.ofReservation(reservation);
        }
        return MyReservationResponse.ofReservationWaiting(reservation, findRankByCreateAt(reservation));
    }

    @Transactional
    public void delete(final Long reservationId) {
        Reservation reservation = reservationRepository.findReservationById(reservationId);
        updateReservationStatus(reservation);
        paymentService.refundPayment(reservation);
        reservationRepository.deleteById(reservationId);
    }

    private void updateReservationStatus(final Reservation reservation) {
        if (reservation.isStandBy()) {
            return;
        }
        reservationRepository.findAllByDateAndTimeAndThemeOrderByCreateAtAsc(
                        reservation.getDate(),
                        reservation.getReservationTime(),
                        reservation.getTheme()
                ).stream()
                .filter(r -> r.isStandBy())
                .findFirst()
                .ifPresent(Reservation::approve);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByMemberAndThemeAndPeriod(final Long memberId, final Long themeId,
                                                                      final String from, final String to) {
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);
        return reservationRepository.findAllByMemberIdAndThemeIdAndDateBetween(memberId, themeId, dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllWaiting() {
        List<Reservation> reservations = reservationRepository.findAllByStatus(Status.STANDBY);
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }
}
