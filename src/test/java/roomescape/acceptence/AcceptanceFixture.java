package roomescape.acceptence;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Cookie;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.specification.RequestSpecification;
import roomescape.client.PaymentClient;
import roomescape.config.DatabaseCleaner;

@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceFixture {
    protected static Cookie normalToken = new Cookie
            .Builder("token", "{일반_권한_JWT_토큰_값}")
            .build();
    protected static Cookie adminToken = new Cookie
            .Builder("token", "{일반_권한_JWT_토큰_값}")
            .build();

    protected RequestSpecification spec;
    @MockBean
    private PaymentClient paymentClient;
    @LocalServerPort
    private int port;
    @Autowired
    private DatabaseCleaner cleaner;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(provider))
                .build();
        RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(paymentClient));
    }

    @AfterEach
    void tearDown() {
        cleaner.cleanUp();
    }
}
