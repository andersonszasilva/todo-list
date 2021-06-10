package br.com.acmecorporation.user.endpoint;

import br.com.acmecorporation.user.service.TokenService;
import br.com.acmecorporation.user.endpoint.request.UserRequest;
import br.com.acmecorporation.user.endpoint.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationEndpoint {

    private AuthenticationManager authenticationManager;
    private TokenService tokenService;

    @Autowired
    public AuthenticationEndpoint(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> authenticate(@RequestBody UserRequest request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = request.convert();
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            String token  = tokenService.generateToken(authentication);

            return  ResponseEntity.ok(new UserResponse(token, "Bearer"));
        } catch (AuthenticationException e) {
             return ResponseEntity.badRequest().build();
        }
    }
}
