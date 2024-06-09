package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.service.dto.response.ReservationResponse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ReservationManageService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final Clock clock;

    public ReservationManageService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository,
            Clock clock
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.clock = clock;
    }

    @Transactional
    public ReservationResponse addReservationByAdmin(ReservationCreateRequest request) {
        Reservation reservation = createValidatedReservation(request);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    @Transactional
    public Reservation addReservation(ReservationCreateRequest request) {
        Reservation reservation = createValidatedReservation(request);
        return reservationRepository.save(reservation);
    }

    private Reservation createValidatedReservation(ReservationCreateRequest request) {
        Member member = getMember(request.memberId());
        ReservationTime reservationTime = getTime(request.timeId());
        Theme theme = getTheme(request.themeId());
        Reservation reservation = request.toReservation(reservationTime, theme, member);
        reservation.validateFutureReservation(LocalDateTime.now(clock));
        validateDuplicatedReservation(reservation);
        return reservation;
    }

    private Member getMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    private ReservationTime getTime(long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 테마입니다."));
    }

    private void validateDuplicatedReservation(Reservation reservation) {
        Optional<Reservation> optionalReservation = reservationRepository.findByDateAndTimeAndThemeAndStatusIs(
                reservation.getDate(), reservation.getTime(), reservation.getTheme(), ReservationStatus.ACCEPTED);
        optionalReservation.ifPresent(r -> {
            throw new IllegalArgumentException("해당 날짜/시간에 이미 예약이 존재합니다.");
        });
    }

    @Transactional
    public void delete(Reservation reservation) {
        reservationRepository.findById(reservation.getId())
                .ifPresent(reservationRepository::delete);
    }

    @Transactional
    public Reservation cancel(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약입니다."));
        reservation.cancel();
        return reservation;
    }

    @Transactional
    public void rollbackCancellation(Reservation reservation) {
        reservationRepository.findById(reservation.getId())
                .ifPresent(Reservation::accept);
    }
}
