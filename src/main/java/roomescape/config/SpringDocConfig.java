package roomescape.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    private final RoomeescapeExceptionOperationCustomizer operationCustomizer;

    public SpringDocConfig(RoomeescapeExceptionOperationCustomizer operationCustomizer) {
        this.operationCustomizer = operationCustomizer;
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder()
                .addOperationCustomizer(operationCustomizer)
                .group("관리자")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi memberApiGroup() {
        return GroupedOpenApi.builder()
                .addOperationCustomizer(operationCustomizer)
                .group("회원")
                .pathsToMatch("/**")
                .pathsToExclude("/admin/**", "/login/**", "/themes/ranking")
                .build();
    }

    @Bean
    public GroupedOpenApi otherApiGroup() {
        return GroupedOpenApi.builder()
                .addOperationCustomizer(operationCustomizer)
                .group("비회원")
                .pathsToMatch("/login/**", "/themes/ranking")
                .build();
    }
}
