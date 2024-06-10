package roomescape.config.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;

        if (HttpStatus.valueOf(response.getStatus()).is5xxServerError()) {
            logError(cachingRequest, cachingResponse);
            return;
        }

        if (HttpStatus.valueOf(response.getStatus()).is4xxClientError()) {
            logWarn(cachingRequest, cachingResponse);
            return;
        }

        if (isStaticHandler(handler)) {
            return;
        }

        logInfo(cachingRequest, cachingResponse);
    }

    private void logError(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        logger.error("Request {} {} {}",
                request.getMethod(), request.getRequestURI(), new String(request.getContentAsByteArray()));
        logger.error("Response {} {}", response.getStatus(), new String(response.getContentAsByteArray()));
    }

    private void logWarn(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        logger.warn("Request {} {} {}",
                request.getMethod(), request.getRequestURI(), new String(request.getContentAsByteArray()));
        logger.warn("Response {} {}", response.getStatus(), new String(response.getContentAsByteArray()));
    }

    private boolean isStaticHandler(Object handler) {
        if (handler instanceof ResourceHttpRequestHandler || handler instanceof ParameterizableViewController) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        return (hasControllerAnnotation(handlerMethod) && hasNotResponseBodyAnnotation(handlerMethod));
    }

    private boolean hasControllerAnnotation(HandlerMethod handlerMethod) {
        return handlerMethod.getBean().getClass().isAnnotationPresent(Controller.class);
    }

    private boolean hasNotResponseBodyAnnotation(HandlerMethod handlerMethod) {
        return !handlerMethod.hasMethodAnnotation(ResponseBody.class);
    }

    private void logInfo(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        logger.info("Request {} {} {}",
                request.getMethod(), request.getRequestURI(), new String(request.getContentAsByteArray()));
        logger.info("Response {} {}", response.getStatus(), new String(response.getContentAsByteArray()));
    }
}
