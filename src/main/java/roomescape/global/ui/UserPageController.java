package roomescape.global.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/reservation")
    public String userReservationPage() {
        return "reservation";
    }

    @GetMapping("/reservation-mine")
    public String userReservationsInfoPage() {
        return "reservation-mine";
    }

    @GetMapping("/login")
    public String userLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String userSignupPage() {
        return "signup";
    }
}
