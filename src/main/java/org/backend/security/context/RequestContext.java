package org.backend.security.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Component
@RequestScope
@Getter
@Setter
public class RequestContext {

    String preferredUserName;

    List<String> roles;

    String jwtHeader;

    String sessionId;

    String kcUserId;

    String personaId;

    String ipAddress;

    String persona;

}