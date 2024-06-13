package roomescape.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin")
    public String adminPage() {
        return "admin/index";
    }

    @GetMapping("/admin/reservation")
    public String adminReservationPage() {
        return "admin/reservation-new";
    }

    @GetMapping("/admin/reservation/waiting")
    public String adminWaitingReservationPage() {
        return "admin/waiting";
    }

    @GetMapping("/admin/time")
    public String timePage() {
        return "admin/time";
    }

    @GetMapping("/admin/theme")
    public String adminThemePage() {
        return "admin/theme";
    }

    @GetMapping("/admin/payment")
    public String adminPaymentPage() {
        return "admin/payment";
    }
}
