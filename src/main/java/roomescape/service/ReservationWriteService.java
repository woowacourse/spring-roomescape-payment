package roomescape.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.AdminReservationRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.controller.request.ReservationWithPaymentRequest;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.*;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

import java.time.LocalDate;

@Transactional
@Service
public class ReservationWriteService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationWriteService(ReservationRepository reservationRepository,
                                   ReservationTimeRepository reservationTimeRepository,
                                   ThemeRepository themeRepository,
                                   MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public Reservation addReservation(ReservationRequest request, Member member) {
        ReservationTime reservationTime = getReservationTime(request.date(), request.timeId(),
                request.themeId());
        Theme theme = getTheme(request.themeId());

        Reservation reservation = Reservation.of(request.date(), reservationTime, theme, member, ReservationStatus.RESERVED);
        return reservationRepository.save(reservation);
    }

    public Reservation addReservation(AdminReservationRequest request) {
        ReservationTime reservationTime = getReservationTime(request.date(), request.timeId(), request.themeId());

        Theme theme = getTheme(request.themeId());
        Member member = getMember(request.memberId());

        Reservation reservation = Reservation.paymentWaitingStatusOf(request.date(), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    private ReservationTime getReservationTime(LocalDate date, long timeId, long themeId) {
        ReservationTime reservationTime = getReservationTime(timeId);
        validateDuplicatedReservation(date, themeId, timeId);
        return reservationTime;
    }

    private void validateDuplicatedReservation(LocalDate date, Long themeId, Long timeId) {
        ReservationTime reservationTime = getReservationTime(timeId);
        Theme theme = getTheme(themeId);

        boolean exists = reservationRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);
        if (exists) {
            throw new DuplicatedException("이미 해당 시간에 예약이 존재합니다.");
        }
    }

    public void deleteReservation(long id) {
        validateExistReservation(id);
        reservationRepository.deleteById(id);
    }

    private void validateExistReservation(long id) {
        boolean exists = reservationRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(id));
        }
    }

    public Reservation updateReservationStatus(ReservationWithPaymentRequest request, Member member) {
        Reservation reservation = getById(reservationRepository, request.reservationId());
        reservation.changeStatus();
        return reservationRepository.save(reservation);
    }

    private <T> T getById(CrudRepository<T, Long> repository, Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(id)));
    }

    private Theme getTheme(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 테마가 존재하지 않습니다.".formatted(id)));
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 사용자가 존재하지 않습니다.".formatted(id)));
    }

    private ReservationTime getReservationTime(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id)));
    }
}
