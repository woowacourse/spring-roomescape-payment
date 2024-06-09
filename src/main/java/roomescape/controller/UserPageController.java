package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {
    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @GetMapping("/")
    public String bestThemePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/reservation-mine")
    public String reservationMinePage() {
        return "reservation-mine";
    }

    @GetMapping("/payment")
    public String paymentPage() {
        return "payment";
    }
}
