package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.WaitingRequest;
import roomescape.exception.BadRequestException;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.Waiting;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.WaitingRepository;

import java.time.LocalDate;

@Transactional
@Service
public class WaitingWriteService {

    private final WaitingRepository waitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public WaitingWriteService(WaitingRepository waitingRepository,
                               ReservationTimeRepository reservationTimeRepository,
                               ThemeRepository themeRepository,
                               ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public Waiting addWaiting(WaitingRequest request, Member member) {
        ReservationTime reservationTime = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(request.timeId())));
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 테마가 존재하지 않습니다.".formatted(request.themeId())));

        validateWaitingInExistingReservation(theme, request.date(), reservationTime, member);
        validateDuplicatedWaiting(theme, request.date(), reservationTime, member);

        Waiting waiting = new Waiting(request.date(), reservationTime, theme, member);
        return waitingRepository.save(waiting);
    }

    private void validateWaitingInExistingReservation(Theme theme, LocalDate date, ReservationTime time, Member member) {
        boolean duplicated = reservationRepository.existsReservationByThemeAndDateAndTimeAndMember(theme, date, time, member);
        if (duplicated) {
            throw new BadRequestException("현재 이름(%s)으로 예약 내역이 이미 존재합니다.".formatted(member.getName()));
        }
    }

    private void validateDuplicatedWaiting(Theme theme, LocalDate date, ReservationTime time, Member member) {
        boolean duplicated = waitingRepository.existsWaitingByThemeAndDateAndTimeAndMember(theme, date, time, member);
        if (duplicated) {
            throw new BadRequestException("현재 이름(%s)으로 예약된 예약 대기 내역이 이미 존재합니다.".formatted(member.getName()));
        }
    }

    public void deleteWaiting(long id) {
        validateExistWaiting(id);
        waitingRepository.deleteById(id);
    }

    private void validateExistWaiting(long id) {
        boolean exists = waitingRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException("해당 id:[%s] 값으로 예약된 예약 대기 내역이 존재하지 않습니다.".formatted(id));
        }
    }
}
