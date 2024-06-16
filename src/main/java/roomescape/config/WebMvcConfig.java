package roomescape.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.provider.model.TokenProvider;
import roomescape.auth.resolver.TokenResolver;
import roomescape.infra.MemberRoleInterceptor;
import roomescape.member.service.MemberService;

@Tag(name = "Spring Web MVC에 사용될 설정", description = "TokenResolver를 리졸버(요청을 특정 객체로 변환)로 등록하고 인터셉터(요청 가로채기)로 MemberRoleInterceptor를 추가한다.")
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String TIME_FORMAT = "HH:mm";

    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @Autowired
    public WebMvcConfig(TokenProvider tokenProvider, MemberService memberService) {
        this.tokenProvider = tokenProvider;
        this.memberService = memberService;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localTimeSerializerCustomizer() {
        return builder -> builder.serializers(new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TokenResolver(tokenProvider));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberRoleInterceptor(memberService, tokenProvider));
    }
}
