package roomescape.admin;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class AdminInterceptorConfig implements WebMvcConfigurer {

    private final AdminHandlerInterceptor adminHandlerInterceptor;

    public AdminInterceptorConfig(AdminHandlerInterceptor adminHandlerInterceptor) {
        this.adminHandlerInterceptor = adminHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminHandlerInterceptor)
                .addPathPatterns("/admin/**");
    }
}
