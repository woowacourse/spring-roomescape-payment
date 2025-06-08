package roomescape.global.restdocs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Attributes.Attribute;

@Configuration
public class RestDocsConfiguration {

    public static Attribute field(final String key, final String value) {
        return new Attribute(key, value);
    }

    @Bean
    public RestDocumentationResultHandler write() {
        return MockMvcRestDocumentation.document(
                "{class-name}/{method-name}",
                Preprocessors.preprocessRequest(
                        Preprocessors.removeHeaders("Content-Length", "Host", "X-CSRF-TOKEN"),
                        Preprocessors.prettyPrint()
                ),
                Preprocessors.preprocessResponse(
                        Preprocessors.modifyHeaders()
                                .remove("Vary")
                                .remove("Content-Length")
                                .remove("X-Content-Type-Options")
                                .remove("X-XSS-Protection")
                                .remove("Cache-Control")
                                .remove("Pragma")
                                .remove("Expires")
                                .remove("X-Frame-Options")
                                .remove("Transfer-Encoding")
                                .remove("Date")
                                .remove("Keep-Alive")
                                .remove("Connection"),
                        Preprocessors.prettyPrint()
                )
        );
    }
}
