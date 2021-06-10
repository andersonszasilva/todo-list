package br.com.acmecorporation.task.endpoint;

import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.task.endpoint.request.TaskStatusRequest;
import br.com.acmecorporation.task.endpoint.response.TaskListResponse;
import br.com.acmecorporation.user.domain.User;
import br.com.acmecorporation.user.service.UserService;
import br.com.acmecorporation.task.domain.Task;
import br.com.acmecorporation.task.endpoint.request.TaskRequet;
import br.com.acmecorporation.task.endpoint.response.TaskResponse;
import br.com.acmecorporation.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
public class TaskEndpoint {

    private TaskService taskService;
    private UserService userService;

    @Autowired
    public TaskEndpoint(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequet request, Authentication authentication) {
        String username = authentication.getName();
        User loggedUser = userService.findByUsername(username);
        Task task = taskService.save(request.create(loggedUser));
        return new ResponseEntity<>(new TaskResponse(task), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestParam(required = false) TaskStatus status, Authentication authentication) {
        String username = authentication.getName();
        User loggedUser = userService.findByUsername(username);
        List<Task> tasks = taskService.findAllBy(loggedUser, status);
        return ResponseEntity.ok(new TaskListResponse(tasks).getTasks());
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> changeTaskStatus(@PathVariable Long id, @RequestBody TaskStatusRequest status, Authentication authentication) {
        String username = authentication.getName();
        User loggedUser = userService.findByUsername(username);
        Task task = taskService.changeTaskStatus(id, status.convertTaskStatus(), loggedUser);
        return new ResponseEntity<>(new TaskResponse(task), HttpStatus.OK);
    }

}
