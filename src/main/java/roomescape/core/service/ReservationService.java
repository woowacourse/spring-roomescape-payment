package roomescape.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Member;
import roomescape.core.domain.Payment;
import roomescape.core.domain.Reservation;
import roomescape.core.domain.ReservationTime;
import roomescape.core.domain.Status;
import roomescape.core.domain.Theme;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.core.repository.ReservationTimeRepository;
import roomescape.core.repository.ThemeRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(final ReservationRepository reservationRepository,
                              final ReservationTimeRepository reservationTimeRepository,
                              final ThemeRepository themeRepository,
                              final MemberRepository memberRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationResponse create(final ReservationRequest request) {
        final Member member = getMember(request);
        final ReservationTime reservationTime = getReservationTime(request);
        final Theme theme = getTheme(request);
        final Reservation reservation = new Reservation(
                member, request.getDate(), reservationTime, theme, Status.findStatus(request.getStatus()),
                LocalDateTime.now());

        validateDuplicateReservation(Status.findStatus(request.getStatus()), reservation);
        reservation.validateDateAndTime();

        final Reservation savedReservation = reservationRepository.save(reservation);
        return new ReservationResponse(savedReservation.getId(), savedReservation);
    }

    private Member getMember(final ReservationRequest request) {
        return memberRepository.findById(request.getMemberId())
                .orElseThrow(IllegalArgumentException::new);
    }

    private ReservationTime getReservationTime(final ReservationRequest request) {
        return reservationTimeRepository.findById(request.getTimeId())
                .orElseThrow(IllegalArgumentException::new);
    }

    private Theme getTheme(final ReservationRequest request) {
        return themeRepository.findById(request.getThemeId())
                .orElseThrow(IllegalArgumentException::new);
    }

    private void validateDuplicateReservation(final Status status, final Reservation reservation) {
        int count = 0;
        if (status == Status.BOOKED) {
            count = reservationRepository.countByDateAndTimeAndTheme(
                    reservation.getDate(), reservation.getReservationTime(), reservation.getTheme());
        }
        if (status == Status.STANDBY) {
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
        List<Reservation> reservations = reservationRepository.findAllByMember(member);
        return reservations.stream()
                .map(reservation -> {
                    Payment payment = paymentRepository.findByReservationId(reservation.getId());
                    return getMyReservationResponse(reservation, payment);
                })
                .toList();
    }

    private MyReservationResponse getMyReservationResponse(final Reservation reservation, final Payment payment) {
        if (reservation.getStatus().equals(Status.BOOKED)) {
            return MyReservationResponse.ofReservation(reservation.getId(), reservation.getTheme().getName(),
                    reservation.getDateString(), reservation.getReservationTime().getStartAtString(),
                    reservation.getStatus().getValue(), payment.getPaymentKey(), payment.getAmount());
        }
        return MyReservationResponse.ofReservationWaiting(reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDateString(), reservation.getReservationTime().getStartAtString(),
                reservation.getStatus().getValue(), findRankByCreateAt(reservation), payment.getPaymentKey(),
                payment.getAmount());
    }

    private Integer findRankByCreateAt(final Reservation reservation) {
        return reservationRepository.countByCreateAtRank(reservation.getDate(), reservation.getReservationTime(),
                reservation.getTheme(), reservation.getCreateAt());
    }

    @Transactional
    public void delete(final Long id) {
        Reservation delete = reservationRepository.findReservationById(id);
        updateReservationStatus(delete);
        paymentRepository.deleteByReservationId(id);
        reservationRepository.deleteById(id);
    }

    private void updateReservationStatus(final Reservation delete) {
        if (delete.getStatus().equals(Status.STANDBY)) {
            return;
        }
        List<Reservation> reservations = reservationRepository.findAllByDateAndTimeAndThemeOrderByCreateAtAsc(
                delete.getDate(), delete.getReservationTime(), delete.getTheme());
        reservations.stream()
                .filter(reservation -> reservation.getStatus().equals(Status.STANDBY))
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
