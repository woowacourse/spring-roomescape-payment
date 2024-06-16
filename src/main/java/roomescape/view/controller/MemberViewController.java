package roomescape.view.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "멤버 뷰 컨트롤러", description = "멤버 예약 정보 뷰 제공")
@Controller
public class MemberViewController {

    @GetMapping("/member/registration")
    public String memberReservationPage() {
        return "reservation-mine";
    }
}
