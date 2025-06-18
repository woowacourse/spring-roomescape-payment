package roomescape.view;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
@ExtendWith(RestDocumentationExtension.class)
class RegularViewTest {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(provider))
                .build();
    }

    @Test
    void accessMyReservationPage() {
        RestAssured.given(spec).log().all()
                .filter(document("view-pages/사용자-예약-페이지"))
                .when().get("/reservation-mine")
                .then().log().all()
                .statusCode(200);
    }
}
