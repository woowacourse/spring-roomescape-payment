package roomescape.service;

import static roomescape.exception.RoomescapeExceptionType.FORBIDDEN_DELETE;
import static roomescape.exception.RoomescapeExceptionType.NOT_FOUND_MEMBER;
import static roomescape.exception.RoomescapeExceptionType.NOT_FOUND_RESERVATION;
import static roomescape.exception.RoomescapeExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.RoomescapeExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.RoomescapeExceptionType.PAST_TIME_RESERVATION;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.AdminReservationRequest;
import roomescape.dto.LoginMemberRequest;
import roomescape.dto.ReservationDetailResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.exception.RoomescapeException;
import roomescape.exception.RoomescapeExceptionType;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Service
public class ReservationService {
    private static final int BOOKED_INDEX = 1;

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              ReservationTimeRepository reservationTimeRepository,
                              ThemeRepository themeRepository,
                              MemberRepository memberRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationResponse saveByUser(LoginMemberRequest loginMemberRequest,
                                          ReservationRequest reservationRequest) {
        ReservationTime requestedTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME));
        Theme requestedTheme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME));
        Member requestedMember = memberRepository.findById(loginMemberRequest.id())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));
        Payment notPayed = paymentRepository.getNotPayed();

        return saveReservation(
                new Reservation(null, reservationRequest.date(), requestedTime, requestedTheme, requestedMember,
                        LocalDateTime.now(), ReservationStatus.WAITING, notPayed));
    }

    public ReservationResponse saveByAdmin(AdminReservationRequest reservationRequest) {
        ReservationTime requestedTime = reservationTimeRepository.findById(reservationRequest.timeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION_TIME));
        Theme requestedTheme = themeRepository.findById(reservationRequest.themeId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME));
        Member requestedMember = memberRepository.findById(reservationRequest.memberId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));
        Payment notPayed = paymentRepository.getNotPayed();

        return saveReservation(
                new Reservation(null, reservationRequest.date(), requestedTime, requestedTheme, requestedMember,
                        LocalDateTime.now(), ReservationStatus.WAITING, notPayed));
    }

    private ReservationResponse saveReservation(Reservation reservation) {
        if (reservation.isBefore(LocalDateTime.now())) {
            throw new RoomescapeException(PAST_TIME_RESERVATION);
        }
        Reservation savedReservation = reservationRepository.save(reservation);
        if (reservationRepository.calculateIndexOf(savedReservation) == BOOKED_INDEX) {
            savedReservation.book();
        }
        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public void pay(long reservationId, Payment payment) {
        reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION))
                .purchase(payment);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationDetailResponse> findAllByMemberId(long userId) {
        List<Reservation> reservationsByMember = reservationRepository.findAllByMemberId(userId);
        return reservationsByMember.stream()
                .map(reservation -> ReservationDetailResponse.from(
                        reservation,
                        reservationRepository.calculateIndexOf(reservation)))
                .toList();
    }

    public List<ReservationResponse> searchReservation(Long themeId, Long memberId, LocalDate dateFrom,
                                                       LocalDate dateTo) {
        return findReservationsBy(themeId, memberId, dateFrom, dateTo).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    private List<Reservation> findReservationsBy(Long themeId, Long memberId, LocalDate dateFrom,
                                                 LocalDate dateTo) {
        if (themeId != null && memberId != null) {
            return reservationRepository.findByThemeIdAndMemberIdAndDateBetween(themeId, memberId, dateFrom, dateTo);
        }
        if (themeId != null) {
            return reservationRepository.findByThemeIdAndDateBetween(themeId, dateFrom, dateTo);
        }
        if (memberId != null) {
            return reservationRepository.findByMemberIdAndDateBetween(memberId, dateFrom, dateTo);
        }
        return reservationRepository.findByDateBetween(dateFrom, dateTo);
    }

    public void deleteByUser(LoginMemberRequest loginMemberRequest, long reservationId) {
        Optional<Reservation> findResult = reservationRepository.findById(reservationId);
        if (findResult.isEmpty()) {
            return;
        }

        Reservation requestedReservation = findResult.get();
        if (requestedReservation.getMember().getId() != loginMemberRequest.id()) {
            throw new RoomescapeException(FORBIDDEN_DELETE);
        }
        reservationRepository.delete(requestedReservation);
        reservationRepository.updateFirstWaiting(requestedReservation);
    }

    public void deleteWaitingByAdmin(long id) {
        Optional<Reservation> findResult = reservationRepository.findById(id);
        if (findResult.isEmpty()) {
            return;
        }

        Reservation requestedReservation = findResult.get();
        if (isNotWaiting(requestedReservation)) {
            throw new RoomescapeException(FORBIDDEN_DELETE);
        }
        reservationRepository.deleteById(id);
    }

    private boolean isNotWaiting(Reservation requestedReservation) {
        return reservationRepository.calculateIndexOf(requestedReservation) == BOOKED_INDEX;
    }

    public List<ReservationResponse> findAllRemainedWaiting() {
        return reservationRepository.findAllRemainedWaiting(LocalDateTime.now()).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public void validateReservationsAuthority(long id, LoginMemberRequest loginMemberRequest) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION));
        if (! reservation.isMemberIdOf(loginMemberRequest.id())) {
            throw new RoomescapeException(RoomescapeExceptionType.NOT_MEMBERS_RESERVATION);
        }
    }
}
