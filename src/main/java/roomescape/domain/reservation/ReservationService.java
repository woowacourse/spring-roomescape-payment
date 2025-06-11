package roomescape.domain.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.auth.dto.LoginMember;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.dto.AdminFilterReservationRequest;
import roomescape.domain.reservation.dto.AdminReservationRequest;
import roomescape.domain.reservation.dto.MineReservationResponse;
import roomescape.domain.reservation.dto.ReservationPaymentRequest;
import roomescape.domain.reservation.dto.ReservationRequest;
import roomescape.domain.reservation.dto.ReservationResponse;
import roomescape.domain.reservation.dto.payment.PaymentRequest;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.custom.reason.reservation.ReservationConflictException;
import roomescape.exception.custom.reason.reservation.ReservationNotExistsMemberException;
import roomescape.exception.custom.reason.reservation.ReservationNotExistsPendingException;
import roomescape.exception.custom.reason.reservation.ReservationNotExistsThemeException;
import roomescape.exception.custom.reason.reservation.ReservationNotExistsTimeException;
import roomescape.exception.custom.reason.reservation.ReservationNotFoundException;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;
    private final PaymentManager paymentManager;
    private final ReservationTimeManager timeManager;

    @Transactional
    public ReservationResponse create(final ReservationPaymentRequest request, final LoginMember loginMember) {
        final ReservationTime reservationTime = getReservationTimeById(request.reservationRequest().timeId());
        final Theme theme = getThemeById(request.reservationRequest().themeId());
        final Member member = getMemberByEmail(loginMember.email());

        final LocalDateTime currentTimestamp = timeManager.todayCurrentTime();
        final ReservationDate reservationDate = ReservationDate.of(request.reservationRequest().date(),
                currentTimestamp.toLocalDate());

        validateDuplicatePending(reservationDate, reservationTime, theme);

        final Reservation notSavedReservation = Reservation.of(reservationDate, member, reservationTime, theme,
                ReservationStatus.PENDING, currentTimestamp);
        final Reservation savedReservation = reservationRepository.save(notSavedReservation);

        final PaymentRequest paymentRequest = request.paymentRequest();
        paymentManager.confirmPayment(paymentRequest);
        final ReservationPayment reservationPayment =
                new ReservationPayment(paymentRequest.paymentKey(), paymentRequest.amount(), savedReservation);
        reservationPaymentRepository.save(reservationPayment);

        return ReservationResponse.from(savedReservation);
    }

    public ReservationResponse createForAdmin(final AdminReservationRequest request) {
        final ReservationTime reservationTime = getReservationTimeById(request.timeId());
        final Theme theme = getThemeById(request.themeId());
        final Member member = getMemberById(request.memberId());

        final LocalDateTime currentTimestamp = timeManager.todayCurrentTime();
        final ReservationDate reservationDate = ReservationDate.of(request.date(), currentTimestamp.toLocalDate());

        validateDuplicatePending(reservationDate, reservationTime, theme);

        final Reservation notSavedReservation = Reservation.of(reservationDate, member, reservationTime, theme,
                ReservationStatus.PENDING, currentTimestamp);
        final Reservation savedReservation = reservationRepository.save(notSavedReservation);
        return ReservationResponse.from(savedReservation);
    }

    public ReservationResponse createWaiting(
            final ReservationRequest request, final LoginMember loginMember
    ) {
        final ReservationTime reservationTime = getReservationTimeById(request.timeId());
        final Theme theme = getThemeById(request.themeId());
        final Member member = getMemberByEmail(loginMember.email());
        final LocalDateTime currentTimestamp = timeManager.todayCurrentTime();
        final ReservationDate reservationDate = ReservationDate.of(request.date(), currentTimestamp.toLocalDate());

        validateNotExistsPending(reservationDate, reservationTime, theme);
        validateDuplicateMember(reservationDate, reservationTime, theme, member);

        final Reservation notSavedReservation = Reservation.of(reservationDate, member, reservationTime, theme,
                ReservationStatus.WAITING, currentTimestamp);
        final Reservation savedReservation = reservationRepository.save(notSavedReservation);
        return ReservationResponse.from(savedReservation);
    }

    public List<MineReservationResponse> readAllMine(final LoginMember loginMember) {
        final Member member = getMemberByEmail(loginMember.email());
        return Stream.concat(
                reservationRepository.findAllWaitingRankByMember(member)
                        .stream()
                        .map(MineReservationResponse::from),

                reservationPaymentRepository.findAllByMember(member)
                        .stream()
                        .map(MineReservationResponse::from)
        ).toList();
    }

    public List<ReservationResponse> readAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> readAllByMemberAndThemeAndDateRange(final AdminFilterReservationRequest request) {
        final Theme theme = getThemeById(request.themeId());
        final Member member = getMemberById(request.memberId());
        final ReservationDate fromDate = ReservationDate.fromQuery(request.from());
        final ReservationDate toDate = ReservationDate.fromQuery(request.to());

        return reservationRepository.findAllByMemberAndThemeAndDateBetween(
                        member, theme,
                        fromDate, toDate
                ).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> readAllWaiting() {
        return reservationRepository.findAllByReservationStatus(ReservationStatus.WAITING).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteById(final Long id) {
        final Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(ReservationNotFoundException::new);

        if (reservation.isPending()) {
            pendingNextReservation(reservation);
        }

        reservationRepository.deleteById(id);
    }

    private void pendingNextReservation(final Reservation reservation) {
        final List<Reservation> waitingReservations = reservationRepository.findAllByDateAndReservationTimeAndThemeAndReservationStatusOrderByAsc(
                reservation.getDate(),
                reservation.getReservationTime(),
                reservation.getTheme(),
                ReservationStatus.WAITING
        );
        pendingToFirstReservation(waitingReservations);
    }

    private void pendingToFirstReservation(final List<Reservation> waitingReservations) {
        if (!waitingReservations.isEmpty()) {
            final Reservation nextReservation = waitingReservations.getFirst();
            nextReservation.pending();
        }
    }

    private void validateDuplicatePending(
            final ReservationDate date, final ReservationTime reservationTime,
            final Theme theme
    ) {
        if (reservationRepository.existsDuplicateStatus(
                reservationTime, date, theme, ReservationStatus.PENDING)) {
            throw new ReservationConflictException();
        }
    }

    private void validateNotExistsPending(
            final ReservationDate date, final ReservationTime reservationTime,
            final Theme theme
    ) {
        if (!reservationRepository.existsDuplicateStatus(
                reservationTime, date, theme, ReservationStatus.PENDING)) {
            throw new ReservationNotExistsPendingException();
        }
    }

    private void validateDuplicateMember(final ReservationDate date, final ReservationTime reservationTime,
                                         final Theme theme,
                                         final Member member) {
        if (reservationRepository.existsByDuplicateMember(
                date, reservationTime, theme, member)) {
            throw new ReservationConflictException();
        }
    }

    private Theme getThemeById(final Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(ReservationNotExistsThemeException::new);
    }

    private ReservationTime getReservationTimeById(final Long reservationTimeId) {
        return reservationTimeRepository.findById(reservationTimeId)
                .orElseThrow(ReservationNotExistsTimeException::new);
    }

    private Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(ReservationNotExistsMemberException::new);
    }

    private Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(ReservationNotExistsMemberException::new);
    }
}
