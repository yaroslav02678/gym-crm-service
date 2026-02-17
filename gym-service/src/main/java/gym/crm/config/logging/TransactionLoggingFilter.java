package gym.crm.config.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class TransactionLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(TransactionLoggingFilter.class);
    private static final String TX_ID_HEADER = "X-Transaction-Id";
    private static final String MDC_KEY = "transactionId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String transactionId = httpRequest.getHeader(TX_ID_HEADER);
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, transactionId);

        log.info("Transaction Started: Method=[{}], URI=[{}], Client-IP=[{}]",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                request.getRemoteAddr());

        try {
            chain.doFilter(request, response);
        } finally {
            log.info("Transaction Finished: Method=[{}], URI=[{}], Status=[{}]",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus());

            MDC.remove(MDC_KEY);
        }
    }
}