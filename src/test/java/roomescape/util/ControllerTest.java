package roomescape.util;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.admin.AdminHandlerInterceptor;
import roomescape.login.LoginMemberArgumentResolver;
import roomescape.login.infrastructure.JwtTokenProvider;
import roomescape.member.fixture.MemberFixture;
import roomescape.member.service.MemberService;
import roomescape.util.restdocs.RestDocsConfiguration;

@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfiguration.class)
public abstract class ControllerTest {

    private static final String ADMIN_TOKEN = "admin-token";
    protected static final Cookie ADMIN_COOKIE = new Cookie("token", ADMIN_TOKEN);

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected LoginMemberArgumentResolver loginMemberArgumentResolver;

    @Autowired
    protected AdminHandlerInterceptor adminHandlerInterceptor;

    @MockBean
    protected MemberService memberService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider restDocumentation) {
        setUpMockMvcRestDocs(context, restDocumentation);
        setUpAdminAccessor();
        setUpMemberAccessor();
    }

    private void setUpMockMvcRestDocs(WebApplicationContext context,
                                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();
    }

    void setUpAdminAccessor() {
        when(jwtTokenProvider.getAccessorId(ADMIN_TOKEN)).thenReturn(100L);
        when(memberService.findById(100L)).thenReturn(MemberFixture.ADMIN_MEMBER);
    }

    void setUpMemberAccessor() {

    }
}
