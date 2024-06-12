package roomescape.controller;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcOperationPreprocessorsConfigurer;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.UriModifyingOperationPreprocessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.application.auth.JwtTokenManager;
import roomescape.application.member.MemberService;
import roomescape.application.payment.PaymentService;
import roomescape.application.reservation.ReservationLookupService;
import roomescape.application.reservation.ReservationService;
import roomescape.application.reservation.ReservationTimeService;
import roomescape.application.reservation.ThemeService;
import roomescape.presentation.auth.AuthController;
import roomescape.presentation.auth.CredentialContext;
import roomescape.presentation.member.MemberController;
import roomescape.presentation.reservation.AdminReservationController;
import roomescape.presentation.reservation.PaymentController;
import roomescape.presentation.reservation.ReservationController;
import roomescape.presentation.reservation.ReservationTimeController;
import roomescape.presentation.reservation.ReservationWaitingController;
import roomescape.presentation.reservation.ThemeController;

@WebMvcTest({
        ReservationController.class,
        AuthController.class,
        MemberController.class,
        AdminReservationController.class,
        PaymentController.class,
        ReservationTimeController.class,
        ReservationWaitingController.class,
        ThemeController.class
})
@ExtendWith(RestDocumentationExtension.class)
abstract class ControllerTest {
    private MockMvcRequestSpecification spec;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected ReservationService reservationService;

    @MockBean
    protected ReservationTimeService reservationTimeService;

    @MockBean
    protected ReservationLookupService reservationLookupService;

    @MockBean
    protected ThemeService themeService;

    @MockBean
    protected PaymentService paymentService;

    @MockBean
    protected JwtTokenManager tokenManager;

    @MockBean
    protected CredentialContext credentialContext;

    @BeforeEach
    void setUpRestDocs(WebApplicationContext context, RestDocumentationContextProvider provider) {
        UriModifyingOperationPreprocessor uriModifier = modifyUris()
                .scheme("http")
                .host("woowa.hoony.me")
                .port(8080);
        HeadersModifyingOperationPreprocessor requestHeaderModifier = modifyHeaders()
                .remove(HttpHeaders.CONTENT_LENGTH);
        HeadersModifyingOperationPreprocessor responseHeaderModifier = modifyHeaders()
                .remove(HttpHeaders.DATE)
                .remove("Keep-Alive")
                .remove(HttpHeaders.CONNECTION)
                .remove(HttpHeaders.TRANSFER_ENCODING)
                .remove(HttpHeaders.CONTENT_LENGTH)
                .remove(HttpHeaders.VARY)
                .set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        MockMvcOperationPreprocessorsConfigurer configurer = documentationConfiguration(provider)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint(), uriModifier, requestHeaderModifier)
                .withResponseDefaults(prettyPrint(), responseHeaderModifier);

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(configurer)
                .build();

        spec = RestAssuredMockMvc.given()
                .mockMvc(mockMvc);
    }

    protected MockMvcRequestSpecification givenWithSpec() {
        return spec;
    }
}
