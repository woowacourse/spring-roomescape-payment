package roomescape.core.service;

import static roomescape.core.exception.ExceptionMessage.ALREADY_BOOKED_TIME_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.MEMBER_NOT_FOUND_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.RESERVATION_NOT_FOUND_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.THEME_NOT_FOUND_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.TOKEN_NOT_FOUND_EXCEPTION;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Member;
import roomescape.core.domain.Payment;
import roomescape.core.domain.PaymentStatus;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationStatus;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Theme;
import roomescape.core.domain.Waiting;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.core.repository.ReservationTimeRepository;
import roomescape.core.repository.ThemeRepository;
import roomescape.core.repository.WaitingRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(final ReservationRepository reservationRepository,
                              final ReservationTimeRepository reservationTimeRepository,
                              final ThemeRepository themeRepository,
                              final MemberRepository memberRepository,
                              final WaitingRepository waitingRepository,
                              final PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationResponse create(final ReservationRequest request) {
        final Reservation reservation = createReservation(request);
        reservation.validateDateAndTime();

        final Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationResponse(savedReservation.getId(), savedReservation);
    }

    private Reservation createReservation(final ReservationRequest request) {
        final Member member = getMemberById(request.getMemberId());
        final String date = request.getDate();
        final ReservationTime reservationTime = getReservationTimeById(request.getTimeId());
        final Theme theme = getThemeById(request.getThemeId());

        final Reservation reservation = new Reservation(member, date, reservationTime, theme, ReservationStatus.BOOKED);

        validateDuplicatedReservation(reservation, reservationTime);

        return reservation;
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));
    }

    private ReservationTime getReservationTimeById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TOKEN_NOT_FOUND_EXCEPTION.getMessage()));
    }

    private Theme getThemeById(final Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(THEME_NOT_FOUND_EXCEPTION.getMessage()));
    }

    private void validateDuplicatedReservation(final Reservation reservation, final ReservationTime reservationTime) {
        final Integer reservationCount = reservationRepository.countByDateAndTimeAndThemeAndStatus(
                reservation.getDate(), reservationTime, reservation.getTheme(), ReservationStatus.BOOKED);
        if (reservationCount > 0) {
            throw new IllegalArgumentException(ALREADY_BOOKED_TIME_EXCEPTION.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll()
                .stream()
                .filter(reservation -> reservation.getStatus().equals(ReservationStatus.BOOKED))
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyReservationResponse> findAllByMember(final LoginMember loginMember) {
        final Member member = getMemberById(loginMember.getId());

        final List<MyReservationResponse> waitings = getWaitingResponses(member);
        final List<MyReservationResponse> reservations = getReservationResponses(member);

        return addAllResponses(List.of(reservations, waitings));
    }

    private List<MyReservationResponse> getWaitingResponses(final Member member) {
        return waitingRepository.findAllWithRankByMember(member)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    private List<MyReservationResponse> getReservationResponses(final Member member) {
        final List<Reservation> myReservations = reservationRepository.findAllByMemberAndStatus(member,
                ReservationStatus.BOOKED);
        final List<Payment> myPayments = paymentRepository.findAllByMemberAndStatus(member, PaymentStatus.CONFIRMED);

        final List<MyReservationResponse> paidResponses = getPaidReservations(myPayments);
        final List<MyReservationResponse> nonPaidResponses = getNonPaidReservations(myReservations, myPayments);
        return addAllResponses(List.of(paidResponses, nonPaidResponses));
    }

    private List<MyReservationResponse> getPaidReservations(final List<Payment> myPayments) {
        return myPayments.stream()
                .map(payment -> MyReservationResponse.from(payment.getReservation(), payment))
                .toList();
    }

    private List<MyReservationResponse> getNonPaidReservations(final List<Reservation> reservations,
                                                               final List<Payment> payments) {
        return reservations.stream()
                .filter(reservation -> payments.stream()
                        .noneMatch(payment -> payment.getReservation().equals(reservation)))
                .map(MyReservationResponse::from)
                .toList();
    }

    private List<MyReservationResponse> addAllResponses(final List<List<MyReservationResponse>> responses) {
        return responses.stream()
                .flatMap(Collection::stream)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByMemberAndThemeAndPeriod(final Long memberId, final Long themeId,
                                                                      final String from, final String to) {
        final Member member = getMemberById(memberId);
        final Theme theme = getThemeById(themeId);
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);

        return reservationRepository.findAllByMemberAndThemeAndDateBetween(member, theme, dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional
    public void delete(final long id, final LoginMember loginMember) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_EXCEPTION.getMessage()));
        final Member requester = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        reservation.cancel(requester);
        changeFirstWaitingToReservation(reservation);
    }

    private void changeFirstWaitingToReservation(final Reservation reservation) {
        final LocalDate date = reservation.getDate();
        final ReservationTime time = reservation.getReservationTime();
        final Theme theme = reservation.getTheme();

        if (waitingRepository.existsByDateAndTimeAndTheme(date, time, theme)) {
            final Waiting waiting = waitingRepository.findFirstByDateAndTimeAndTheme(date, time, theme);
            final Member member = waiting.getMember();
            final String dateString = date.format(DateTimeFormatter.ISO_DATE);

            final Reservation nextReservation = new Reservation(member, dateString, time, theme,
                    ReservationStatus.BOOKED);

            waitingRepository.delete(waiting);
            reservationRepository.save(nextReservation);
        }
    }

    @Transactional
    public void deleteByAdmin(final long id, final LoginMember loginMember) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_EXCEPTION.getMessage()));
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage()));

        reservation.cancel(member);
        changeFirstWaitingToReservation(reservation);
    }
}
