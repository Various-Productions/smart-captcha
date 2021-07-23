/*
 * Copyright (c) 2021 InTroubleDE.
 * All rights reserved.
 */

package me.lucacw.smartcaptcha.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import me.lucacw.smartcaptcha.config.imp.ServerSettingsConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CommonsLog
@Component
@RequiredArgsConstructor
public final class ApiKeyFilter extends GenericFilterBean {

    private final ServerSettingsConfig config;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) return;

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        String header = httpServletRequest.getHeader("API-KEY");
        String remoteAddress = httpServletRequest.getRemoteHost();

        if (!this.config.getApiKey().equals(header) && !this.config.getWhitelistedIps().contains(remoteAddress)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please provide a valid X-API-KEY Header!");
            log.debug("Incoming request from address '" + remoteAddress + "' failed api-key-auth on route: " + requestURI + " Url was: " + ((HttpServletRequest) request).getRequestURL());

            return;
        }

        log.debug("Incoming request from source '" + remoteAddress + "' passed filter on route: " + requestURI + " Url was: " + ((HttpServletRequest) request).getRequestURL());
        chain.doFilter(request, response);
    }
}