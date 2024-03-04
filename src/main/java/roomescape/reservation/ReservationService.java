package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.member.LoginMember;
import roomescape.member.MemberService;
import roomescape.theme.Theme;
import roomescape.theme.ThemeRepository;
import roomescape.time.Time;
import roomescape.time.TimeRepository;

import java.util.List;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private MemberService memberService;
    private TimeRepository timeRepository;
    private ThemeRepository themeRepository;

    public ReservationService(ReservationRepository reservationRepository, MemberService memberService, TimeRepository timeRepository, ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.memberService = memberService;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationResponse save(LoginMember loginMember, ReservationRequest reservationRequest) {
        Time time = timeRepository.findById(reservationRequest.getTime()).orElseThrow(RuntimeException::new);
        Theme theme = themeRepository.findById(reservationRequest.getTheme()).orElseThrow(RuntimeException::new);
        String name = reservationRequest.getName() == null ? findMemberName(loginMember) : reservationRequest.getName();

        Reservation reservation = reservationRepository.save(new Reservation(name, reservationRequest.getDate(), time, theme));

        return new ReservationResponse(reservation.getId(), reservation.getName(), reservation.getTheme().getName(), reservation.getDate(), reservation.getTime().getValue());
    }

    private String findMemberName(LoginMember loginMember) {
        return memberService.findById(loginMember.getMemberId()).getName();
    }

    public void deleteById(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }
}
