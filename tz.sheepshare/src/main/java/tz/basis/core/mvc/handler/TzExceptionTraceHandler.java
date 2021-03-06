package tz.basis.core.mvc.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * ExceptionViewResolver에서 예외를 처리하여 리턴하였을 경우 HandlerInterceptor.afterCompletion에 발생한 예외가 전달되지 않는다.
 * 예외에 대한 특별한 처리가 있을 경우 본 인터페이스를 구현하여 처리하도록 한다.
 *
 * @author TZ
 *
 */
public interface TzExceptionTraceHandler {

    void handler(HttpServletRequest request, HttpServletResponse response, Exception exception);

}
