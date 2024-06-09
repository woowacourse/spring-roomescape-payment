package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.INVALID_DATE;
import static roomescape.exception.RoomescapeExceptionCode.MEMBER_NOT_FOUND;
import static roomescape.exception.RoomescapeExceptionCode.PAYMENT_NOT_FOUND;
import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_ALREADY_EXISTS;
import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_NOT_FOUND;
import static roomescape.exception.RoomescapeExceptionCode.RESERVATION_TIME_NOT_FOUND;
import static roomescape.exception.RoomescapeExceptionCode.THEME_NOT_FOUND;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
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
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Transactional
@Service
public class ReservationService {

    private static final int MAX_RESERVATIONS_PER_TIME = 1;
    private static final int INCREMENT_VALUE_FOR_RANK = 1;

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(
            final ReservationRepository reservationRepository,
            final MemberRepository memberRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ThemeRepository themeRepository, PaymentRepository paymentRepository
    ) {
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

        final Reservation reservation = reservationDto.toModel(member, time, theme, ReservationStatus.RESERVED);
        validateDate(reservation.getDate());
        validateDuplicatedReservation(reservation);
        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    private void validateDate(final LocalDate date) {
        if (date.isBefore(LocalDate.now()) || date.equals(LocalDate.now())) {
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

    public void delete(final Long id) {
        final boolean exists = reservationRepository.existsById(id);
        if (!exists) {
            throw new RoomescapeException(RESERVATION_NOT_FOUND);
        }
        reservationRepository.deleteById(id);
    }

    public List<MyReservationWithRankResponse> findMyReservationsAndWaitings(final LoginMember loginMember) {
        final List<Reservation> reservationsByMemberId = reservationRepository.findByMemberId(loginMember.id());
        final List<Reservation> reservations = reservationRepository.findAll();
        return reservationsByMemberId.stream()
                .map(reservation -> new MyReservationWithRankResponse(
                        reservation,
                        calculateRank(reservations, reservation),
                        findPaymentByReservationId(reservation.getId())
                )).toList();
    }

    private Payment findPaymentByReservationId(final long reservationId) {
        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomescapeException(PAYMENT_NOT_FOUND));
    }

    private Long calculateRank(final List<Reservation> reservations, final Reservation reservation) {
        return reservations.stream()
                .filter(r -> Objects.equals(r.getDate(), reservation.getDate()) &&
                        Objects.equals(r.getTheme().getId(), reservation.getTheme().getId()) &&
                        Objects.equals(r.getTime().getId(), reservation.getTime().getId()) &&
                        r.getStatus() == reservation.getStatus() &&
                        r.getId() < reservation.getId())
                .count() + INCREMENT_VALUE_FOR_RANK;
    }
}
