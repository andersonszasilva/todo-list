package br.com.acmecorporation.user.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationEndpointTest {

    private final String URL = "/auth";
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Usuário cadastrado deve conseguir recuperar um token de acesso")
    void authenticate() throws Exception {
        // Given
        String newTask ="{\"username\":\"wile.coyote@acmecorporation.com\", \"password\": \"123456\"}";

        //When
        ResultActions actions = mockMvc.perform(post(URL)
                .content(newTask)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        //Then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expirationDate").exists());
    }

    @Test
    @DisplayName("Caso o usuário digite dados incorretos a aplicação não retornar erro 400")
    void notAuthenticate() throws Exception {
        // Given
        String newTask ="{\"username\":\"wile@acmecorporation.com\", \"password\": \"12\"}";

        //When
        ResultActions actions = mockMvc.perform(post(URL)
                .content(newTask)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        //Then
        actions.andExpect(status().isBadRequest());
    }

}