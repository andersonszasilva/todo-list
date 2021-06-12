package br.com.acmecorporation.task.endpoint;

import br.com.acmecorporation.task.repository.TaskRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskEndpointTest {

    private final String URL = "/todo";
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        repository.deleteAll();
    }

    @Test
    @WithUserDetails("wile.coyote@acmecorporation.com")
    @DisplayName("Usuário logado deve conseguir criar uma nova tarefa")
    void createTask() throws Exception {
        // Given
        String request ="{\"summary\": \"Ir ao mercado\",\"description\": \"Comprar cervejas\"}";

        //When
        ResultActions actions = this.mockMvc.perform(post(URL)
                .content(request)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        //Then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.summary").value("Ir ao mercado"))
                .andExpect(jsonPath("$.description").value("Comprar cervejas"));
    }

    @Test
    @WithUserDetails("wile.coyote@acmecorporation.com")
    @DisplayName("Usuário logado deve listar as suas próprias tarefas ordenadas pelo estado pending")
    void getTasks() throws Exception {
        // Given
        this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao mercado\",\"description\": \"Comprar cervejas\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // And
        MvcResult result = this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao barbeiro\",\"description\": \"Fazer barba e corte de cabelo\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());
        Long id = json.getLong("id");

        this.mockMvc.perform(patch(URL + "/{id}", id)
                .content("{\"status\": \"COMPLETED\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        //When
        ResultActions actions = this.mockMvc.perform(get(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        //Then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].status").value("PENDING"));
    }


    @Test
    @WithUserDetails("super.user@acmecorporation.com")
    @DisplayName("Super usuário  deve conseguir listar tarefas de outros usuários")
    void getTasksWithSuperUser() throws Exception {
        // Given
        MvcResult result = this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao barbeiro\",\"description\": \"Fazer barba e corte de cabelo\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        //When
        ResultActions actions = this.mockMvc.perform(get(URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        //Then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].status").value("PENDING"))
                .andExpect(jsonPath("$.[0].user").exists());
    }

    @Test
    @WithUserDetails("wile.coyote@acmecorporation.com")
    @DisplayName("Usuário logado deve conseguir mudar o estado da sua própria tarefa")
    void changeTaskStatus() throws Exception {
        // Given
        MvcResult result = this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao mercado\",\"description\": \"Comprar cervejas\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());
        Long id = json.getLong("id");

        //When
        ResultActions actions = this.mockMvc.perform(patch(URL + "/{id}", id)
                .content("{\"status\": \"COMPLETED\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }



    @Test
    @WithUserDetails("wile.coyote@acmecorporation.com")
    @DisplayName("Usuário logado deve conseguir visualizar a sua tarefa")
    void getTask() throws Exception {
        // Given
        MvcResult result = this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao mercado\",\"description\": \"Comprar cervejas\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());
        Long id = json.getLong("id");

        //When
        ResultActions actions = this.mockMvc.perform(get(URL + "/{id}", id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Ir ao mercado"))
                .andExpect(jsonPath("$.description").value("Comprar cervejas"));

    }

    @Test
    @WithUserDetails("wile.coyote@acmecorporation.com")
    @DisplayName("Usuário logado deve conseguir editar a sua tarefa")
    void testUpdateTask() throws Exception {
        // Given
        MvcResult result = this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao mercado\",\"description\": \"Comprar cervejas\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());
        Long id = json.getLong("id");

        //When
        ResultActions actions = this.mockMvc.perform(put(URL + "/{id}", id)
                .content("{\"summary\": \"Ir ao mercado da esquina\",\"description\": \"Comprar cervejas e leite\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Ir ao mercado da esquina"))
                .andExpect(jsonPath("$.description").value("Comprar cervejas e leite"));

    }

    @Test
    @WithUserDetails("wile.coyote@acmecorporation.com")
    @DisplayName("Usuário logado deve conseguir deletar a sua tarefa")
    void deleteTask() throws Exception {
        // Given
        MvcResult result = this.mockMvc.perform(post(URL)
                .content("{\"summary\": \"Ir ao mercado\",\"description\": \"Comprar cervejas\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        JSONObject json = new JSONObject(result.getResponse().getContentAsString());
        Long id = json.getLong("id");

        //When
        ResultActions actions = this.mockMvc.perform(delete(URL + "/{id}", id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        // Then
        actions.andExpect(status().isNoContent());
    }

}