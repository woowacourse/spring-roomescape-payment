package roomescape.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserPageController {
    @GetMapping("/reservation")
    public String getReservationPage() {
        return "reservation";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String getSignupPage() {
        return "signup";
    }

    @GetMapping("/reservation-mine")
    public String getReservationMinePage() {
        return "reservation-mine";
    }

    @GetMapping("/payment")
    public String getPaymentPage(
            Model model,
            @RequestParam int amount,
            @RequestParam long reservationId) {
        model.addAttribute("amount", amount);
        model.addAttribute("reservationId", reservationId);
        return "payment";
    }
}
