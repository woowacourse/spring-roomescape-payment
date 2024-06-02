package roomescape.core.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Member;
import roomescape.core.domain.Payment;
import roomescape.core.domain.PaymentStatus;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Role;
import roomescape.core.domain.Theme;
import roomescape.core.domain.Waiting;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.dto.reservation.WebPaidReservationResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.core.repository.ReservationTimeRepository;
import roomescape.core.repository.ThemeRepository;
import roomescape.core.repository.WaitingRepository;
import roomescape.infrastructure.PaymentApprover;

@Service
public class ReservationService {
    protected static final String MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE = "존재하지 않는 회원입니다.";
    protected static final String TIME_NOT_EXISTS_EXCEPTION_MESSAGE = "존재하지 않는 예약 시간입니다.";
    protected static final String THEME_NOT_EXISTS_EXCEPTION_MESSAGE = "존재하지 않는 테마입니다.";
    protected static final String ALREADY_BOOKED_TIME_EXCEPTION_MESSAGE
            = "해당 시간에 이미 예약 내역이 존재합니다.";
    protected static final String RESERVATION_NOT_EXISTS_EXCEPTION_MESSAGE = "존재하지 않는 예약입니다.";
    protected static final String RESERVATION_IS_NOT_YOURS_EXCEPTION_MESSAGE
            = "본인의 예약만 취소할 수 있습니다.";
    protected static final String NOT_ALLOWED_TO_MEMBER_EXCEPTION_MESSAGE = "관리자만 예약을 취소할 수 있습니다.";

    private final PaymentApprover paymentApprover;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(final PaymentApprover paymentApprover,
                              final ReservationRepository reservationRepository,
                              final ReservationTimeRepository reservationTimeRepository,
                              final ThemeRepository themeRepository,
                              final MemberRepository memberRepository,
                              final WaitingRepository waitingRepository,
                              final PaymentRepository paymentRepository) {
        this.paymentApprover = paymentApprover;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationResponse create(final ReservationRequest request) {
        final Reservation reservation
                = createReservation(request, PaymentStatus.ACCOUNT_TRANSFERRED);
        final Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationResponse(savedReservation);
    }

    @Transactional
    public WebPaidReservationResponse createAndPay(final ReservationRequest reservationRequest,
                                                   final PaymentConfirmRequest paymentRequest) {
        final Reservation reservation
                = createReservation(reservationRequest, PaymentStatus.WEB_PAID);
        final PaymentConfirmResponse paymentResponse
                = paymentApprover.confirmPayment(paymentRequest);
        final Payment savedPayment = paymentRepository.save(paymentResponse.toPayment());
        final Reservation savedReservation = saveWithPayment(reservation, savedPayment);
        return new WebPaidReservationResponse(savedReservation);
    }

    private Reservation createReservation(final ReservationRequest request,
                                          final PaymentStatus paymentStatus) {
        final Member member = getMemberById(request.getMemberId());
        final String date = request.getDate();
        final ReservationTime reservationTime = getReservationTimeById(request.getTimeId());
        final Theme theme = getThemeById(request.getThemeId());

        final Reservation reservation
                = new Reservation(member, date, reservationTime, theme, paymentStatus);
        reservation.validateDateAndTime();
        validateDuplicatedReservation(reservation, reservationTime);
        return reservation;
    }

    private Reservation saveWithPayment(final Reservation reservation, final Payment payment) {
        final Reservation reservationWithPayment = reservation.withPayment(payment);
        return reservationRepository.save(reservationWithPayment);
    }

    private Member getMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE));
    }

    private ReservationTime getReservationTimeById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(TIME_NOT_EXISTS_EXCEPTION_MESSAGE));
    }

    private Theme getThemeById(final Long id) {
        return themeRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(THEME_NOT_EXISTS_EXCEPTION_MESSAGE));
    }

    private void validateDuplicatedReservation(final Reservation reservation,
                                               final ReservationTime reservationTime) {
        final Integer reservationCount = reservationRepository.countByDateAndTimeAndTheme(
                reservation.getDate(), reservationTime, reservation.getTheme());
        if (reservationCount > 0) {
            throw new IllegalArgumentException(ALREADY_BOOKED_TIME_EXCEPTION_MESSAGE);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MyReservationResponse> findAllByMember(final LoginMember loginMember) {
        final Member member = getMemberById(loginMember.getId());

        final List<MyReservationResponse> waitings = getResponsesByWaiting(member);
        final List<MyReservationResponse> reservations = getResponsesByReservation(member);
        reservations.addAll(waitings);

        return reservations;
    }

    private List<MyReservationResponse> getResponsesByReservation(final Member member) {
        return new ArrayList<>(reservationRepository.findAllByMember(member)
                .stream()
                .map(MyReservationResponse::from)
                .toList());
    }

    private List<MyReservationResponse> getResponsesByWaiting(final Member member) {
        return waitingRepository.findAllWithRankByMember(member)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllByMemberAndThemeAndPeriod(final Long memberId,
                                                                      final Long themeId,
                                                                      final String from,
                                                                      final String to) {
        final Member member = getMemberById(memberId);
        final Theme theme = getThemeById(themeId);
        final LocalDate dateFrom = LocalDate.parse(from);
        final LocalDate dateTo = LocalDate.parse(to);

        return reservationRepository
                .findAllByMemberAndThemeAndDateBetween(member, theme, dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional
    public void delete(final long id, final LoginMember loginMember) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        RESERVATION_NOT_EXISTS_EXCEPTION_MESSAGE));
        final Long reservationMemberId = reservation.getMember().getId();
        final Long loginMemberId = loginMember.getId();

        if (!reservationMemberId.equals(loginMemberId)) {
            throw new IllegalArgumentException(RESERVATION_IS_NOT_YOURS_EXCEPTION_MESSAGE);
        }

        reservationRepository.delete(reservation);
        changeFirstWaitingToReservation(reservation);
    }

    private void changeFirstWaitingToReservation(final Reservation reservation) {
        final LocalDate date = reservation.getDate();
        final ReservationTime time = reservation.getReservationTime();
        final Theme theme = reservation.getTheme();

        if (waitingRepository.existsByDateAndTimeAndTheme(date, time, theme)) {
            final Waiting waiting
                    = waitingRepository.findFirstByDateAndTimeAndTheme(date, time, theme);
            final Member member = waiting.getMember();
            final String dateString = date.format(DateTimeFormatter.ISO_DATE);

            final Reservation nextReservation
                    = new Reservation(member, dateString, time, theme, PaymentStatus.PENDING);

            waitingRepository.delete(waiting);
            reservationRepository.save(nextReservation);
        }
    }

    @Transactional
    public void deleteByAdmin(final long id, final LoginMember loginMember) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        RESERVATION_NOT_EXISTS_EXCEPTION_MESSAGE));
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(
                        () -> new IllegalArgumentException(MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE));

        if (member.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException(NOT_ALLOWED_TO_MEMBER_EXCEPTION_MESSAGE);
        }

        reservationRepository.delete(reservation);
        changeFirstWaitingToReservation(reservation);
    }
}
