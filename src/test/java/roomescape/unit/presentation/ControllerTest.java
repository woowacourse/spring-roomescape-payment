package roomescape.unit.presentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.service.AuthService;
import roomescape.business.service.MemberService;
import roomescape.business.service.PaymentService;
import roomescape.business.service.ReservationService;
import roomescape.business.service.ReservationTimeService;
import roomescape.business.service.ThemeService;
import roomescape.business.service.WaitingService;
import roomescape.presentation.api.AuthApiController;
import roomescape.presentation.api.MemberApiController;
import roomescape.presentation.api.PaymentApiController;
import roomescape.presentation.api.ReservationApiController;
import roomescape.presentation.api.ReservationTimeApiController;
import roomescape.presentation.api.ThemeApiController;
import roomescape.presentation.api.WaitingApiController;

@WebMvcTest(value = {
        AuthApiController.class,
        MemberApiController.class,
        PaymentApiController.class,
        ReservationApiController.class,
        ReservationTimeApiController.class,
        ThemeApiController.class,
        WaitingApiController.class,
        JwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    WaitingService waitingService;

    @MockitoBean
    ThemeService themeService;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    ReservationTimeService reservationTimeService;

    @MockitoBean
    ReservationService reservationService;

    @MockitoBean
    PaymentService paymentService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }
}
