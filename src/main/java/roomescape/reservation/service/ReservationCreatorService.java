package roomescape.reservation.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.service.MemberService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationInfo;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.ThemeService;

@Service
public class ReservationCreatorService {

    private final ReservationService reservationService;
    private final ThemeService themeService;
    private final MemberService memberService;
    private final ReservationTimeService reservationTimeService;

    public ReservationCreatorService(final ReservationService reservationService, final ThemeService themeService,
                                     final MemberService memberService,
                                     final ReservationTimeService reservationTimeService) {
        this.reservationService = reservationService;
        this.themeService = themeService;
        this.memberService = memberService;
        this.reservationTimeService = reservationTimeService;
    }


    @Transactional
    public Reservation createReservation(ReservationRequest request, Long memberId) {
        reservationService.checkIfReservationExists(request);
        ReservationTime time = reservationTimeService.findReservationTime(request.timeId());
        Theme theme = themeService.findTheme(request.themeId());
        Member member = memberService.findUserByMemberId(memberId);
        ReservationInfo info = new ReservationInfo(request.date(), time, theme);
        return reservationService.save(Reservation.createUpcomingReservationWithUnassignedId(member, info));
    }
}

