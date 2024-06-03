package roomescape.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.FindMyReservationResponse;
import roomescape.domain.member.Member;

@Service
public class ReservationFacadeService {

    private final UserReservationService userReservationService;
    private final PaymentService paymentService;

    public ReservationFacadeService(UserReservationService userReservationService, PaymentService paymentService) {
        this.userReservationService = userReservationService;
        this.paymentService = paymentService;
    }

    public CreateReservationResponse reserve(String orderId, long amount, String paymentKey,
                                             Long memberId, LocalDate date, Long timeId, Long themeId) {
        paymentService.pay(orderId, amount, paymentKey);
        return userReservationService.reserve(memberId, date, timeId, themeId);
    }

    public CreateReservationResponse standby(Long memberId, LocalDate date, Long timeId, Long themeId) {
        return userReservationService.standby(memberId, date, timeId, themeId);
    }

    public void deleteStandby(Long id, Member member) {
        userReservationService.deleteStandby(id, member);
    }

    public List<FindMyReservationResponse> findMyReservationsWithRank(Long memberId) {
        return userReservationService.findMyReservationsWithRank(memberId);
    }
}
