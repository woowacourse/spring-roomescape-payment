package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.*;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.payment.Payment;
import roomescape.domain.member.Member;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationDto;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.*;

@Transactional
@Service
public class ReservationService {

    private static final int MAX_RESERVATIONS_PER_TIME = 1;
    private static final int INCREMENT_VALUE_FOR_RANK = 1;

    private final Clock clock;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(
            final Clock clock,
            final ReservationRepository reservationRepository,
            final MemberRepository memberRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ThemeRepository themeRepository,
            final PaymentRepository paymentRepository) {
        this.clock = clock;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.paymentRepository = paymentRepository;
    }

    public ReservationResponse createReservation(final ReservationDto reservationDto) {
        final Member member = memberRepository.findById(reservationDto.memberId())
                .orElseThrow(() -> new RoomescapeException(MEMBER_NOT_FOUND));
        final ReservationTime time = reservationTimeRepository.findById(reservationDto.timeId())
                .orElseThrow(() -> new RoomescapeException(RESERVATION_TIME_NOT_FOUND));
        final Theme theme = themeRepository.findById(reservationDto.themeId())
                .orElseThrow(() -> new RoomescapeException(THEME_NOT_FOUND));

        final Reservation reservation = reservationDto.toReservation(member, time, theme, ReservationStatus.RESERVED);
        validateDate(reservation.getDate());
        validateDuplicatedReservation(reservation);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private void validateDate(final LocalDate date) {
        final LocalDate now = LocalDate.now(clock);
        if (date.isBefore(now) || date.equals(now)) {
            throw new RoomescapeException(INVALID_DATE);
        }
    }

    private void validateDuplicatedReservation(final Reservation reservation) {
        final int count = reservationRepository.countByDateAndTimeIdAndThemeId(
                reservation.getDate(), reservation.getTime().getId(), reservation.getTheme().getId()
        );

        if (count >= MAX_RESERVATIONS_PER_TIME) {
            throw new RoomescapeException(RESERVATION_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        final List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBy(final ReservationFilterParam filterParam) {
        final List<Reservation> reservations = reservationRepository.findByThemeIdAndMemberIdAndDateBetweenAndStatus(
                filterParam.themeId(), filterParam.memberId(),
                filterParam.dateFrom(), filterParam.dateTo(), ReservationStatus.RESERVED
        );
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public void cancelReservation(final Reservation reservation) {
        if (reservation.isWaiting()) {
            reservationRepository.delete(reservation);
            return;
        }
        reservation.changeStatus(ReservationStatus.CANCELED);
    }

    public Reservation findReservationById(final Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_NOT_FOUND));
    }

    public List<MyReservationWithRankResponse> findMyReservationsAndWaitings(final LoginMember loginMember) {
        final List<Reservation> reservationsByMemberId = reservationRepository.findByMemberId(loginMember.id());
        return reservationsByMemberId.stream()
                .map(this::createMyReservationResponse)
                .toList();
    }

    private MyReservationWithRankResponse createMyReservationResponse(final Reservation reservation) {
        if (reservation.isReserved()) {
            final Payment payment = paymentRepository.findByReservationAndStatus(reservation, PaymentStatus.PAID)
                    .orElseThrow(() -> new RoomescapeException(PAYMENT_NOT_FOUND));
            return new MyReservationWithRankResponse(reservation, calculateRank(reservation), payment);
        }
        return new MyReservationWithRankResponse(reservation, calculateRank(reservation));
    }

    private Long calculateRank(final Reservation reservation) {
        return reservationRepository.countByDateAndThemeIdAndTimeIdAndStatusAndIdLessThan(
                reservation.getDate(), reservation.getTheme().getId(),
                reservation.getTime().getId(), reservation.getStatus(), reservation.getId()
        ) + INCREMENT_VALUE_FOR_RANK;
    }
}
