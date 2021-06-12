package br.com.acmecorporation.user.endpoint;

import br.com.acmecorporation.user.domain.AccessToken;
import br.com.acmecorporation.user.service.TokenService;
import br.com.acmecorporation.user.endpoint.request.AuthenticationRequest;
import br.com.acmecorporation.user.endpoint.response.AuthenticationResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Api(tags = "/auth")
public class AuthenticationEndpoint {

    private AuthenticationManager authenticationManager;
    private TokenService tokenService;

    @Autowired
    public AuthenticationEndpoint(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Autenticação feita com sucesso"),
            @ApiResponse(code = 400, message = "Usuário e/ou senha inválido(s)")
    })
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            AccessToken accessToken = tokenService.generateToken(authentication);

            return  ResponseEntity.ok(new AuthenticationResponse(accessToken.getToken(), "Bearer", accessToken.getExpirationDate()));
        } catch (AuthenticationException e) {
             return ResponseEntity.badRequest().build();
        }
    }
}
