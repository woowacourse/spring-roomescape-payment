package roomescape.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    @GetMapping("/signup")
    public String signUpPage() {
        return "signup";
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String reservationMinePage() {
        return "reservation-mine";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
