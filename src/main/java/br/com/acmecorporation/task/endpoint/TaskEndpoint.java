package br.com.acmecorporation.task.endpoint;

import br.com.acmecorporation.task.domain.Task;
import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.task.endpoint.request.TaskRequest;
import br.com.acmecorporation.task.endpoint.request.TaskStatusRequest;
import br.com.acmecorporation.task.endpoint.request.TaskUpdateRequest;
import br.com.acmecorporation.task.endpoint.response.TaskResponse;
import br.com.acmecorporation.task.endpoint.response.TaskWithUserResponse;
import br.com.acmecorporation.task.exception.TaskNotFoundException;
import br.com.acmecorporation.task.service.TaskService;
import br.com.acmecorporation.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todo")
@Api(tags = "/todo")
public class TaskEndpoint {

    private static final Logger log = LoggerFactory.getLogger(TaskEndpoint.class);
    private TaskService taskService;

    @Autowired
    public TaskEndpoint(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest request, Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();
        log.info("Criando nova tarefa : {} - Usuário: {}", request, loggedUser.getUsername());

        Task task = taskService.save(request.create(loggedUser));
        log.info("Nova tarefa criada com id: {} - Usuário: {}", task.getId(), loggedUser.getUsername());
        return new ResponseEntity<>(TaskResponse.createResponse(task), HttpStatus.CREATED);
    }

    @GetMapping
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestParam(required = false) TaskStatus status, Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();
        log.info("Listando tarefas com o Usuário: {} - Filtros: {}", loggedUser.getUsername(), status);

        boolean hasSuperUserRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("SUPER_USER"));

        if (hasSuperUserRole) {
            log.info("Listando tarefas com o Usuário Admin: {} - Filtros: {}", loggedUser.getUsername(), status);
            List<Task> tasks = taskService.findAllBy(status);
            log.info("Um total de: {} tarefa(s) listada(s) para o Usuário Admin: {}", tasks.size(), loggedUser.getUsername());
            return ResponseEntity.ok(TaskWithUserResponse.createListResponse(tasks));
        }

        List<Task> tasks = taskService.findAllBy(loggedUser, status);
        log.info("Um total de: {} tarefa(s) listada(s) para o Usuário: {}", tasks.size(), loggedUser.getUsername());
        return ResponseEntity.ok(TaskResponse.createListResponse(tasks));

    }

    @PatchMapping("/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<?> changeTaskStatus(@PathVariable Long id, @RequestBody TaskStatusRequest status, Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();
        log.info("Alterando estado da tarefa com id: {} novo status :{} com usuário: {}", id, status, loggedUser.getUsername());
        try {
            Task task = taskService.changeTaskStatus(id, status.convertTaskStatus(), loggedUser);
            log.info("Estado da tarefa com id: {} alterado para: {} usuário: {}", id, status, loggedUser.getUsername());
            return new ResponseEntity<>(TaskResponse.createResponse(task), HttpStatus.OK);
        } catch (TaskNotFoundException e) {
            log.info("Tarefa de id: {} não encontrada - usuário: {}", id, loggedUser.getUsername());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();
        log.info("Deletando tarefa de id: {} com usuário: {}", id, loggedUser.getUsername());

        Optional<Task> task = taskService.findBy(id, loggedUser);
        if(task.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        taskService.deleteTask(task.get());
        log.info("Tarefa de id: {} removida com usuário: {}", id, loggedUser.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping ("/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<?> getTask(@PathVariable Long id, Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();

        String username = authentication.getName();
        log.info("Visualizando tarefa de id: {} com usuário: {}", id, loggedUser.getUsername());

        Optional<Task> task = taskService.findBy(id, loggedUser);
        if(task.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        log.info("Tarefa de id: {} visualizada pelo usuário: {}", id, username);
        return new ResponseEntity<>(TaskResponse.createResponse(task.get()), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer access_token")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request, Authentication authentication) {
        User loggedUser = (User) authentication.getPrincipal();
        log.info("Alterando tarefa com id: {} novos valores :{} com usuário: {}", id, request, loggedUser.getUsername());

        Optional<Task> task = taskService.findBy(id, loggedUser);
        if(task.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Task saved = taskService.save(request.buildTask(task.get()));
        log.info("Tarefa com id: {} alterada com usuário: {}", id,  loggedUser.getUsername());
        return new ResponseEntity<>(TaskResponse.createResponse(saved), HttpStatus.OK);
    }

}