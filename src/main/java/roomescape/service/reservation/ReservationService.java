package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
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
import roomescape.service.payment.PaymentService;
import roomescape.service.payment.dto.PaymentConfirmInput;
import roomescape.service.reservation.dto.ReservationListResponse;
import roomescape.service.reservation.dto.ReservationMineListResponse;
import roomescape.service.reservation.dto.ReservationMineResponse;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.service.reservation.dto.ReservationSaveInput;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.data.jpa.domain.Specification.where;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasEndDate;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasMemberId;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasStartDate;
import static roomescape.domain.reservation.ReservationRepository.Specs.hasThemeId;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final PaymentService paymentService;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationWaitingRepository reservationWaitingRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              PaymentService paymentService,
                              Clock clock) {
        this.reservationRepository = reservationRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.paymentService = paymentService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ReservationListResponse searchReservation(
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
        List<Reservation> reservations = reservationRepository.findByMemberId(member.getId());
        List<ReservationWaitingWithRank> reservationWaitingWithRanks
                = reservationWaitingRepository.findAllWaitingWithRankByMemberId(member.getId());

        List<ReservationMineResponse> myReservations = Stream.concat(
                        reservations.stream().map(ReservationMineResponse::new),
                        reservationWaitingWithRanks.stream().map(ReservationMineResponse::new)
                )
                .sorted(Comparator.comparing(ReservationMineResponse::retrieveDateTime))
                .toList();
        return new ReservationMineListResponse(myReservations);
    }

    public ReservationResponse saveReservationWithPayment(
            ReservationSaveInput reservationSaveInput, PaymentConfirmInput paymentConfirmInput, Member member) {
        Reservation savedReservation = saveReservation(reservationSaveInput, member);
        paymentService.confirmPayment(paymentConfirmInput, savedReservation);

        return new ReservationResponse(savedReservation);
    }

    public ReservationResponse saveReservationWithoutPayment(ReservationSaveInput reservationSaveInput, Member member) {
        Reservation savedReservation = saveReservation(reservationSaveInput, member);
        return new ReservationResponse(savedReservation);
    }

    private Reservation saveReservation(ReservationSaveInput input, Member member) {
        ReservationTime time = reservationTimeRepository.getReservationTimeById(input.timeId());
        Theme theme = themeRepository.getThemeById(input.themeId());
        Reservation reservation = input.toReservation(time, theme, member);
        validateDuplicateReservation(reservation);
        validateDateTimeReservation(reservation);

        return reservationRepository.save(reservation);
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
        Reservation reservation = reservationRepository.getReservationById(reservationId);
        validateReservationMember(reservation, memberId);

        reservationWaitingRepository.findFirstByReservation(reservation).ifPresentOrElse(
                waiting -> upgradeWaitingToReservationAndDeleteWaiting(reservation, waiting),
                () -> reservationRepository.delete(reservation)
        );
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
