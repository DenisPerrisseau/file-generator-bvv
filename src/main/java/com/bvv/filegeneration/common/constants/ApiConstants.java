package com.bvv.filegeneration.common.constants;

public class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_PREFIX = "/api";
    public static final String TEMPLATES_BASE = API_PREFIX + "/templates";
    public static final String JOBS_BASE = API_PREFIX + "/jobs";
    public static final String LOGS_BASE = API_PREFIX + "/logs";

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final int DEFAULT_TIMEOUT_SECONDS = 30;
    public static final int MAX_RETRIES = 3;
}

