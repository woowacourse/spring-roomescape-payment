package roomescape.domain.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.model.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.payment.service.PaymentService;
import roomescape.domain.reservation.dto.ReservationDto;
import roomescape.domain.reservation.dto.SaveReservationRequest;
import roomescape.domain.reservation.dto.SearchReservationsParams;
import roomescape.domain.reservation.dto.SearchReservationsRequest;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.ReservationWaiting;
import roomescape.domain.reservation.model.Theme;
import roomescape.domain.reservation.repository.CustomReservationRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ReservationWaitingRepository;
import roomescape.domain.reservation.repository.ThemeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final CustomReservationRepository customReservationRepository;
    private final MemberRepository memberRepository;
    private final ThemeRepository themeRepository;
    private final PaymentService paymentService;

    public ReservationService(
            final CustomReservationRepository customReservationRepository,
            final ReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationWaitingRepository reservationWaitingRepository,
            final MemberRepository memberRepository,
            final ThemeRepository themeRepository,
            final PaymentService paymentService
    ) {
        this.customReservationRepository = customReservationRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.memberRepository = memberRepository;
        this.themeRepository = themeRepository;
        this.paymentService = paymentService;
    }

    public List<ReservationDto> getReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationDto::from)
                .toList();
    }

    public List<ReservationDto> searchReservations(final SearchReservationsRequest request) {
        final SearchReservationsParams searchReservationsParams = new SearchReservationsParams(
                request.memberId(),
                request.themeId(),
                request.from(),
                request.to()
        );

        return customReservationRepository.searchReservations(searchReservationsParams)
                .stream()
                .map(ReservationDto::from)
                .toList();
    }

    public ReservationDto saveReservation(final SaveReservationRequest request) {
        final ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 예약 시간이 존재하지 않습니다."));
        final Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 테마가 존재하지 않습니다."));
        final Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 회원이 존재하지 않습니다."));
        final Reservation reservation = request.toReservation(reservationTime, theme, member);

        validateReservationDateAndTime(reservation.getDate(), reservationTime);
        validateReservationDuplicated(reservation);

        paymentService.submitPayment(request.orderId(), request.amount(), request.paymentKey(), member);

        return ReservationDto.from(reservationRepository.save(reservation));
    }

    private static void validateReservationDateAndTime(final ReservationDate date, final ReservationTime time) {
        final LocalDateTime reservationLocalDateTime = LocalDateTime.of(date.getValue(), time.getStartAt());
        if (reservationLocalDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("현재 날짜보다 이전 날짜를 예약할 수 없습니다.");
        }
    }

    private void validateReservationDuplicated(final Reservation reservation) {
        if (reservationRepository.existsByDateAndTime_IdAndTheme_Id(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId())
        ) {
            throw new IllegalArgumentException("이미 해당 날짜/시간의 테마 예약이 있습니다.");
        }
    }

    @Transactional
    public void deleteReservation(final Long reservationId) {
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 정보입니다."));
        reservationRepository.deleteById(reservation.getId());
        reservationWaitingRepository.findTopByDateAndTimeAndThemeOrderByCreatedAtAsc(
                        reservation.getDate(),
                        reservation.getTime(),
                        reservation.getTheme())
                .ifPresent(this::saveReservationWithWaiting);
    }

    private void saveReservationWithWaiting(final ReservationWaiting reservationWaiting) {
        final Reservation reservation = reservationWaiting.makeReservation();
        reservationRepository.save(reservation);
        reservationWaitingRepository.delete(reservationWaiting);
    }

    public List<ReservationDto> getMyReservations(final Long memberId) {
        return reservationRepository.findAllByMember_Id(memberId)
                .stream()
                .map(ReservationDto::from)
                .toList();
    }
}
