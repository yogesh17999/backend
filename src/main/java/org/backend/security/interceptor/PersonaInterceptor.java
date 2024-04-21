package org.backend.security.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.security.context.RequestContext;
import org.backend.security.contstant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
public class PersonaInterceptor extends OncePerRequestFilter {

    @Autowired
    RequestContext requestContext;

    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String personaId= request.getHeader(SecurityConstants.PERSONA_ID);
        String persona = request.getHeader(SecurityConstants.PERSONA);
        if (null != personaId) {
            requestContext.setPersonaId(personaId);

        }
        if(persona !=null){
            requestContext.setPersona(persona);
        }
        filterChain.doFilter(request, response);
    }

}
