package ru.kalita.ratelimit.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    private RequestUtils() {
    }

    private static final String UNKNOWN_ADDRESS = "unknown";
    private static final String COMMA = ",";
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public static String extractClientIp(HttpServletRequest request) {
        for (String header: IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (StringUtils.hasText(ipList) && !UNKNOWN_ADDRESS.equalsIgnoreCase(ipList)) {
                return ipList.split(COMMA)[0];
            }
        }
        return request.getRemoteAddr();
    }
}
