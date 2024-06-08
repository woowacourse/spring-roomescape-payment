package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.application.auth.TokenManager;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.member.MemberService;
import roomescape.application.payment.PaymentService;
import roomescape.application.reservation.ReservationLookupService;
import roomescape.application.reservation.ReservationService;
import roomescape.application.reservation.ReservationTimeService;
import roomescape.application.reservation.ThemeService;
import roomescape.domain.member.Role;
import roomescape.exception.UnAuthorizedException;
import roomescape.presentation.auth.AuthController;
import roomescape.presentation.auth.CredentialContext;
import roomescape.presentation.auth.LoginMemberIdArgumentResolver;
import roomescape.presentation.member.MemberController;
import roomescape.presentation.reservation.AdminReservationController;
import roomescape.presentation.reservation.ReservationController;
import roomescape.presentation.reservation.ReservationTimeController;
import roomescape.presentation.reservation.ReservationWaitingController;
import roomescape.presentation.reservation.ThemeController;
import roomescape.presentation.view.AdminController;
import roomescape.presentation.view.ClientController;

@WebMvcTest({
        ReservationController.class,
        MemberController.class,
        ThemeController.class,
        ReservationTimeController.class,
        ReservationController.class,
        ReservationWaitingController.class,
        AdminReservationController.class,
        AuthController.class,
        AdminController.class,
        ClientController.class
})
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public abstract class RestDocsTest {

    protected static final String COOKIE_NAME = "token";

    @MockBean
    protected ReservationService reservationService;
    @MockBean
    protected ReservationLookupService reservationLookupService;
    @MockBean
    protected ReservationTimeService reservationTimeService;
    @MockBean
    protected ThemeService themeService;
    @MockBean
    protected PaymentService paymentService;
    @MockBean
    protected MemberService memberService;
    @MockBean
    protected LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;
    @MockBean
    protected TokenManager tokenManager;
    @MockBean
    private CredentialContext context;
    protected MockMvcRequestSpecification restDocs;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        restDocs = RestAssuredMockMvc.given()
                .mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(documentationConfiguration(restDocumentation)
                                .operationPreprocessors()
                                .withRequestDefaults(prettyPrint()
                                        , modifyUris()
                                                .scheme("https")
                                                .host("docs.roomescape.com")
                                                .removePort()

                                )
                                .withResponseDefaults(prettyPrint()))
                        .build())
                .log().all();

    }

    protected String getAdminToken(Long id, String name) {
        String adminToken = "adminToken";

        doReturn(new TokenPayload(id, name, Role.ADMIN))
                .when(tokenManager)
                .extract(any());

        doReturn(adminToken)
                .when(tokenManager)
                .createToken(any());

        return adminToken;
    }

    protected String getMemberToken(Long id, String name) {
        String memberToken = "memberToken";

        doThrow(new UnAuthorizedException())
                .when(context)
                .validatePermission(Role.ADMIN);

        doReturn(new TokenPayload(id, name, Role.MEMBER))
                .when(tokenManager)
                .extract(any());

        doReturn(memberToken)
                .when(tokenManager)
                .createToken(any());

        return memberToken;
    }
}
