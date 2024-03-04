package roomescape.reservation;

import org.springframework.stereotype.Service;
import roomescape.member.LoginMember;
import roomescape.member.MemberResponse;
import roomescape.member.MemberService;

import java.util.List;

@Service
public class ReservationService {
    private ReservationDao reservationDao;
    private MemberService memberService;

    public ReservationService(ReservationDao reservationDao, MemberService memberService) {
        this.reservationDao = reservationDao;
        this.memberService = memberService;
    }

    public ReservationResponse save(LoginMember loginMember, ReservationRequest reservationRequest) {
        Reservation reservation = reservationDao.save(reservationRequest.getName() == null ? createReservationRequest(loginMember, reservationRequest) : reservationRequest);

        return new ReservationResponse(reservation.getId(), reservation.getName(), reservation.getTheme().getName(), reservation.getDate(), reservation.getTime().getValue());
    }

    private ReservationRequest createReservationRequest(LoginMember loginMember, ReservationRequest reservationRequest) {
        MemberResponse member = memberService.findById(loginMember.getMemberId());
        return new ReservationRequest(member.getName(), reservationRequest.getDate(), reservationRequest.getTheme(), reservationRequest.getTime());
    }

    public void deleteById(Long id) {
        reservationDao.deleteById(id);
    }

    public List<ReservationResponse> findAll() {
        return reservationDao.findAll().stream()
                .map(it -> new ReservationResponse(it.getId(), it.getName(), it.getTheme().getName(), it.getDate(), it.getTime().getValue()))
                .toList();
    }
}
