package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.WaitingRequest;
import roomescape.exception.BadRequestException;
import roomescape.exception.NotFoundException;
import roomescape.model.*;
import roomescape.repository.*;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public WaitingService(WaitingRepository waitingRepository,
                          ReservationTimeRepository reservationTimeRepository,
                          ThemeRepository themeRepository, MemberRepository memberRepository, ReservationRepository reservationRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
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

    public boolean existsWaiting(Theme theme, LocalDate date, ReservationTime time) {
        return waitingRepository.existsWaitingByThemeAndDateAndTime(theme, date, time);
    }

    @Transactional
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

    public List<WaitingWithRank> findMemberWaiting(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new NotFoundException("해당 id:[%s] 값으로 예약된 예약 대기 내역이 존재하지 않습니다.".formatted(memberId)));
        return waitingRepository.findWaitingWithRankByMemberId(member.getId());
    }

    public List<Waiting> findAllWaiting() {
        return waitingRepository.findAll();
    }

    public Waiting getFirstWaitingByCondition(Theme theme, LocalDate date, ReservationTime time) {
        return waitingRepository.findFirstByThemeAndDateAndTime(theme, date, time)
                .orElseThrow(() ->
                        new NotFoundException("해당 테마:[%s], 날짜:[%s], 시간:[%s] 값으로 예약된 예약 대기 내역이 존재하지 않습니다.".formatted(theme.getName(), date, time.getStartAt())));
    }
}