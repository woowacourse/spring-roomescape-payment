package roomescape.service;

import static roomescape.exception.RoomescapeErrorCode.MEMBER_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_ALREADY_EXISTS;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.RESERVATION_TIME_NOT_FOUND;
import static roomescape.exception.RoomescapeErrorCode.THEME_NOT_FOUND;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.AdminReservationSaveRequest;
import roomescape.dto.reservation.MyReservationWithRankResponse;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationWithPaymentRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
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

    public ReservationService(
            final ReservationRepository reservationRepository,
            final MemberRepository memberRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ThemeRepository themeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationResponse createReservation(final AdminReservationSaveRequest request) {
        final Reservation reservation = Reservation.builder()
                .member(getMemberById(request.memberId()))
                .date(request.date())
                .time(getTimeById(request.timeId()))
                .theme(getThemeById(request.themeId()))
                .status(ReservationStatus.RESERVED)
                .build();
        validateDuplicatedReservation(reservation);
        return new ReservationResponse(reservationRepository.save(reservation));
    }

    public ReservationResponse createReservation(final ReservationWithPaymentRequest request, final long memberId) {
        final Reservation reservation = Reservation.builder()
                .member(getMemberById(memberId))
                .date(request.date())
                .time(getTimeById(request.timeId()))
                .theme(getThemeById(request.themeId()))
                .status(ReservationStatus.RESERVED)
                .build();
        validateDuplicatedReservation(reservation);
        reservation.validateDateTime();
        return new ReservationResponse(reservationRepository.save(reservation));
    }

    private void validateDuplicatedReservation(final Reservation reservation) {
        final int count = reservationRepository.countByDateAndTimeAndTheme(
                reservation.getDate(), reservation.getTime(), reservation.getTheme());
        if (count >= MAX_RESERVATIONS_PER_TIME) {
            throw new RoomescapeException(RESERVATION_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll() {
        final List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllBy(final ReservationFilterParam filterParam) {
        final List<Reservation> reservations = reservationRepository.findByThemeIdAndMemberIdAndDateBetweenAndStatus(
                filterParam.themeId(), filterParam.memberId(),
                filterParam.dateFrom(), filterParam.dateTo(), ReservationStatus.RESERVED
        );
        return reservations.stream()
                .map(ReservationResponse::new)
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
                        calculateRank(reservations, reservation)
                )).toList();
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

    public Member getMemberById(final Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new RoomescapeException(MEMBER_NOT_FOUND));
    }

    private ReservationTime getTimeById(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(RESERVATION_TIME_NOT_FOUND));
    }

    private Theme getThemeById(final Long id) {
        return themeRepository.findById(id).orElseThrow(() -> new RoomescapeException(THEME_NOT_FOUND));
    }
}
