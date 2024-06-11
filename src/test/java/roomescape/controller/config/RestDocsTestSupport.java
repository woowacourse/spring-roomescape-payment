package roomescape.controller.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.AuthenticationExtractor;
import roomescape.domain.member.Role;
import roomescape.service.auth.AuthService;
import roomescape.service.dto.request.LoginMember;
import roomescape.service.reservation.pay.PaymentService;

import static org.mockito.ArgumentMatchers.any;

@Disabled
@Import({AuthService.class, AuthenticationExtractor.class, RestDocsConfiguration.class})
@AutoConfigureRestDocs
@ExtendWith({MockitoExtension.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
public abstract class RestDocsTestSupport {

    protected static LoginMember ADMIN;
    protected static LoginMember USER;
    protected static String USER_TOKEN;
    protected static String ADMIN_TOKEN;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
    @MockBean
    protected PaymentService paymentService;
    @MockBean
    private AuthService authService;

    protected static Attributes.Attribute constraints(final String value) {
        return new Attributes.Attribute("constraints", value);
    }

    @PostConstruct
    private void initialize() {
        USER_TOKEN = "adminToken";
        USER = new LoginMember(1L, "testName", Role.USER);
        ADMIN = new LoginMember(1L, "admin", Role.ADMIN);
    }

    @BeforeEach
    void setUp(final WebApplicationContext context,
               final RestDocumentationContextProvider provider) {
        Mockito.when(authService.getTokenName()).thenReturn("token");
        Mockito.when(authService.findMemberByToken(any())).thenReturn(ADMIN);
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .build();
    }

}
