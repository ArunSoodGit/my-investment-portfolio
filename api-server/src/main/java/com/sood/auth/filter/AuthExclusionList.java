package com.sood.auth.filter;

import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Set;

@Singleton
@Log4j2
public class AuthExclusionList {

    private final Set<String> exclusions = new HashSet<>();

    public AuthExclusionList() {
        exclusions.add("POST:/v1/api/auth/login");
        exclusions.add("OPTIONS:/v1/api/auth/login");
        exclusions.add("OPTIONS:/v1/api/portfolio/");
        exclusions.add("POST:/v1/api/auth/register");
        exclusions.add("POST:/v1/api/auth/refresh");
        exclusions.add("GET:/v1/api/health");
        exclusions.add("GET:/v1/api/swagger-ui.html");
        exclusions.add("GET:/v1/api/openapi.yaml");
    }

    public boolean isExcluded(final String method, final String path) {
        final String key = method + ":" + path;
        return exclusions.stream().anyMatch(exclusion -> {
            if (exclusion.contains("*")) {
                final String pattern = exclusion.replace("*", ".*");
                return key.matches(pattern);
            }
            return exclusion.equals(key);
        });
    }
}
