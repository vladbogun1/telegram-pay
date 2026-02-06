package com.example.telegrampay.security;

import com.example.telegrampay.domain.Creator;
import com.example.telegrampay.repository.CreatorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CreatorAuthFilter extends OncePerRequestFilter {
    private final CreatorRepository creatorRepository;

    public CreatorAuthFilter(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/webhooks/")
            || path.startsWith("/ui")
            || path.equals("/")
            || path.startsWith("/assets/")
            || (path.equals("/api/creators") && HttpMethod.POST.matches(request.getMethod()))
            || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String apiKey = request.getHeader("X-Api-Key");
        if (apiKey == null || apiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API key");
            return;
        }
        Optional<Creator> creator = creatorRepository.findByApiKey(apiKey);
        if (creator.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return;
        }
        try {
            CreatorContext.setCreatorId(creator.get().getId());
            filterChain.doFilter(request, response);
        } finally {
            CreatorContext.clear();
        }
    }
}
