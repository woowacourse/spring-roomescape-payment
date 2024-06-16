package roomescape.view.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "메인 뷰 컨트롤러", description = "메인 뷰 반환")
@Controller
public class MainViewController {

    @GetMapping
    public String mainPage() {
        return "index";
    }
}
