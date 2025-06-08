package roomescape.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.service.ReservationService;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final ReservationService reservationService;

    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin/index";
    }

    @GetMapping("/admin/reservation")
    public String getReservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/admin/time")
    public String getTimePage() {
        return "admin/time";
    }

    @GetMapping("/admin/theme")
    public String getThemePage() {
        return "admin/theme";
    }

    @GetMapping("/reservation")
    public String getUserReservationPage() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String getMyReservationsPage() {
        return "reservation-mine";
    }

    @GetMapping("/admin/waiting")
    public String getWaitingReservationsPage() {
        return "admin/waiting";
    }

    @GetMapping("/mypage/{reservationId}/payment")
    public String getPaymentPage(@PathVariable(value = "reservationId") Long reservationId,
                                 LoginMember loginMember,
                                 Model model) {
        reservationService.validateOwnership(reservationId, loginMember.id());
        model.addAttribute("reservationId", reservationId);
        return "reservation-payment";
    }

    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }
}
