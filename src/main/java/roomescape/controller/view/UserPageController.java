package roomescape.controller.view;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[View] User")
@Controller
public class UserPageController {
    @GetMapping("/reservation")
    @Operation(summary = "예약 페이지 호출", description = "예약 페이지를 호출한다.")
    public String getReservationPage() {
        return "reservation";
    }

    @GetMapping("/login")
    @Operation(summary = "로그인 페이지 호출", description = "로그인 페이지를 호출한다.")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    @Operation(summary = "회원가입 페이지 호출", description = "회원가입 페이지를 호출한다.")
    public String getSignupPage() {
        return "signup";
    }

    @GetMapping("/reservation-mine")
    @Operation(summary = "내 예약정보 페이지 호출", description = "내 예약정보 페이지를 호출한다.")
    public String getReservationMinePage() {
        return "reservation-mine";
    }

    @GetMapping("/payment")
    @Operation(summary = "예약 결제 페이지 호출", description = "예약 결제 페이지를 호출한다.")
    public String getPaymentPage(
            Model model,
            @RequestParam int amount,
            @RequestParam long reservationId) {
        model.addAttribute("amount", amount);
        model.addAttribute("reservationId", reservationId);
        return "payment";
    }
}
