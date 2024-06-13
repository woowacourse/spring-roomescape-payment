package roomescape.utils;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import org.springframework.restdocs.RestDocumentationContextProvider;

public class DocumentHelper {
    public static RequestSpecification specification(final RestDocumentationContextProvider restDocumentation) {
        final Filter documentationConfiguration = documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(modifyHeaders().remove("Host"),
                        modifyHeaders().remove("Content-Length"),
                        prettyPrint())
                .withResponseDefaults(modifyHeaders().remove("Transfer-Encoding"),
                        modifyHeaders().remove("Date"),
                        modifyHeaders().remove("Keep-Alive"),
                        modifyHeaders().remove("Connection"),
                        modifyHeaders().remove("Content-Length"),
                        prettyPrint());

        return new RequestSpecBuilder()
                .addFilter(documentationConfiguration)
                .build();
    }
}
