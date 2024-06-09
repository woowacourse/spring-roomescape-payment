package roomescape.service.reservation.module;

import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.theme.Theme;
import roomescape.domain.time.ReservationTime;
import roomescape.dto.reservation.ReservationRequest;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@Component
public class ReservationMapper {

    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationMapper(ReservationTimeRepository timeRepository,
                             ThemeRepository themeRepository,
                             MemberRepository memberRepository
    ) {
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public Reservation mapperOf(ReservationRequest request, Status status) {
        ReservationTime reservationTime = timeRepository.findByIdOrThrow(request.timeId());
        Theme theme = themeRepository.findByIdOrThrow(request.themeId());
        Member member = memberRepository.findByIdOrThrow(request.memberId());
        return request.toEntity(reservationTime, theme, member, status);
    }
}
