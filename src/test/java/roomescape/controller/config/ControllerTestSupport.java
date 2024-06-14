package roomescape.controller.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.cookies.ResponseCookiesSnippet;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;
import roomescape.IntegrationTestSupport;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

public class ControllerTestSupport extends IntegrationTestSupport {

    protected RequestSpecification specification;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        Filter filter = documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(
                        modifyHeaders()
                                .remove("Host")
                                .remove("Content-Length"),
                        prettyPrint())
                .withResponseDefaults(
                        modifyHeaders()
                                .remove("Transfer-Encoding")
                                .remove("Date")
                                .remove("Keep-Alive")
                                .remove("Connection")
                                .remove("Content-Length"),
                        prettyPrint());
        this.specification = new RequestSpecBuilder()
                .addFilter(filter)
                .build();
    }

    protected Filter makeDocumentFilter(QueryParametersSnippet request, ResponseFieldsSnippet response) {
        return document(
                "{class-name}/{method-name}",
                request,
                response);
    }

    protected Filter makeDocumentFilter(RequestCookiesSnippet request, ResponseFieldsSnippet response) {
        return document(
                "{class-name}/{method-name}",
                request,
                response);
    }

    protected Filter makeDocumentFilter(RequestCookiesSnippet requestCookies, QueryParametersSnippet requestParams, ResponseFieldsSnippet response) {
        return document(
                "{class-name}/{method-name}",
                requestCookies,
                requestParams,
                response);
    }

    protected Filter makeDocumentFilter(RequestCookiesSnippet requestCookies, RequestFieldsSnippet requestFields, ResponseFieldsSnippet response) {
        return document(
                "{class-name}/{method-name}",
                requestCookies,
                requestFields,
                response);
    }

    protected Filter makeDocumentFilter(RequestCookiesSnippet request) {
        return document(
                "{class-name}/{method-name}",
                request);
    }

    protected Filter makeDocumentFilter(RequestFieldsSnippet request) {
        return document(
                "{class-name}/{method-name}",
                request);
    }

    protected Filter makeDocumentFilter(RequestFieldsSnippet request, ResponseCookiesSnippet responseCookies) {
        return document(
                "{class-name}/{method-name}",
                request,
                responseCookies);
    }
}
