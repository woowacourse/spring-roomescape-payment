package roomescape.service.reservation;

import static org.springframework.data.jpa.domain.Specification.where;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasEndDate;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasMemberId;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasStartDate;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasThemeId;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationWithPayment;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.domain.reservationwaiting.ReservationWaitingWithRank;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.reservation.DuplicatedReservationException;
import roomescape.exception.reservation.InvalidDateTimeReservationException;
import roomescape.exception.reservation.InvalidReservationMemberException;
import roomescape.exception.reservation.NotFoundReservationException;
import roomescape.exception.reservationtime.NotFoundReservationTimeException;
import roomescape.exception.theme.NotFoundThemeException;
import roomescape.service.payment.PaymentClient;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.payment.dto.PaymentConfirmOutput;
import roomescape.service.reservation.dto.ReservationListResponse;
import roomescape.service.reservation.dto.ReservationMineListResponse;
import roomescape.service.reservation.dto.ReservationMineResponse;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationSaveInput;

@Service
@Transactional
public class ReservationService {
    private static final long RANK_PREFIX = 1L;
    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;
    private final PaymentClient paymentClient;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationWaitingRepository reservationWaitingRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              ReservationPaymentRepository reservationPaymentRepository,
                              PaymentClient paymentClient,
                              Clock clock) {
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationPaymentRepository = reservationPaymentRepository;
        this.paymentClient = paymentClient;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ReservationListResponse findAllReservation(
            Long memberId, Long themeId, LocalDate dateFrom, LocalDate dateTo) {
        List<Reservation> reservations = reservationRepository.findAll(
                where(hasMemberId(memberId))
                        .and(hasThemeId(themeId))
                        .and(hasStartDate(dateFrom))
                        .and(hasEndDate(dateTo))
        );
        return new ReservationListResponse(reservations.stream()
                .map(ReservationResponse::new)
                .toList());
    }

    @Transactional(readOnly = true)
    public ReservationMineListResponse findMyReservation(Member member) {
        List<ReservationWithPayment> reservationsWithPayment
                = reservationRepository.findAllReservationWithPaymentByMemberId(member.getId());
        List<Reservation> reservationsWithoutPayment
                = reservationRepository.findAllReservationWithoutPaymentByMemberId(member.getId());
        List<ReservationWaitingWithRank> reservationWaitingsWithRank
                = findAllWaitingWithRankByMemberId(member.getId());

        List<ReservationMineResponse> myReservations = Stream.of(
                        reservationsWithPayment.stream().map(ReservationMineResponse::new),
                        reservationsWithoutPayment.stream().map(ReservationMineResponse::new),
                        reservationWaitingsWithRank.stream().map(ReservationMineResponse::new)
                )
                .flatMap(s -> s)
                .sorted(Comparator.comparing(ReservationMineResponse::retrieveDateTime))
                .toList();
        return new ReservationMineListResponse(myReservations);
    }

    private List<ReservationWaitingWithRank> findAllWaitingWithRankByMemberId(Long memberId) {
        List<ReservationWaiting> reservationWaitings = reservationWaitingRepository.findAllWaitingByMemberId(memberId);
        return reservationWaitings.stream()
                .map(this::calculateRank)
                .toList();
    }

    private ReservationWaitingWithRank calculateRank(ReservationWaiting reservationWaiting) {
        long rank = reservationWaitingRepository.countByReservationIdAndIdLessThan(
                reservationWaiting.getReservation().getId(), reservationWaiting.getId()) + RANK_PREFIX;
        return new ReservationWaitingWithRank(reservationWaiting, rank);
    }

    public ReservationResponse saveReservationWithoutPayment(ReservationSaveInput reservationSaveInput, Member member) {
        Reservation savedReservation = saveReservation(reservationSaveInput, member);
        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse saveReservationWithPayment(
            ReservationSaveInput reservationSaveInput, PaymentConfirmInput paymentConfirmInput, Member member) {
        Reservation savedReservation = saveReservation(reservationSaveInput, member);
        confirmAndSavePayment(paymentConfirmInput, savedReservation);
        return new ReservationResponse(savedReservation);
    }

    private Reservation saveReservation(ReservationSaveInput input, Member member) {
        ReservationTime reservationTime = findReservationTimeById(input.timeId());
        Theme theme = findThemeById(input.themeId());
        Reservation reservation = input.toReservation(reservationTime, theme, member);
        validateDuplicateReservation(reservation);
        validateDateTimeReservation(reservation);
        return reservationRepository.save(reservation);
    }

    private void confirmAndSavePayment(PaymentConfirmInput input, Reservation reservation) {
        PaymentConfirmOutput output = paymentClient.confirmPayment(input);
        ReservationPayment reservationPayment = output.toReservationPayment(reservation);
        reservationPaymentRepository.save(reservationPayment);
    }

    private ReservationTime findReservationTimeById(long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(NotFoundReservationTimeException::new);
    }

    private Theme findThemeById(long id) {
        return themeRepository.findById(id)
                .orElseThrow(NotFoundThemeException::new);
    }

    private void validateDuplicateReservation(Reservation reservation) {
        if (reservationRepository.existsByInfo(reservation.getInfo())) {
            throw new DuplicatedReservationException();
        }
    }

    private void validateDateTimeReservation(Reservation reservation) {
        if (reservation.isPast(LocalDateTime.now(clock))) {
            throw new InvalidDateTimeReservationException();
        }
    }

    public void deleteReservation(long reservationId, long memberId) {
        Reservation reservation = findReservationById(reservationId);
        validateReservationMember(reservation, memberId);

        reservationWaitingRepository.findFirstByReservation(reservation).ifPresentOrElse(
                waiting -> upgradeWaitingToReservationAndDeleteWaiting(reservation, waiting),
                () -> reservationRepository.delete(reservation)
        );
    }

    private Reservation findReservationById(long id) {
        return reservationRepository.findById(id)
                .orElseThrow(NotFoundReservationException::new);
    }

    private void validateReservationMember(Reservation reservation, long memberId) {
        if (reservation.isNotOwnedBy(memberId)) {
            throw new InvalidReservationMemberException();
        }
    }

    private void upgradeWaitingToReservationAndDeleteWaiting(Reservation reservation, ReservationWaiting waiting) {
        reservation.updateMember(waiting.getMember());
        reservationWaitingRepository.delete(waiting);
    }
}
