package br.com.acmecorporation.task.endpoint.response;

import br.com.acmecorporation.task.domain.Task;

import java.util.List;
import java.util.stream.Collectors;

public class TaskSuperUserResponse extends TaskResponse {

    private UserResponse user;

    private TaskSuperUserResponse(Task task) {
        super(task);
        this.user = new UserResponse(task.getUserId(), task.getOwner());
    }

    public UserResponse getUser() {
        return user;
    }

    public static List<TaskResponse> createListResponse(List<Task> tasks) {
        return tasks.stream()
                .map(TaskSuperUserResponse::new)
                .collect(Collectors.toUnmodifiableList());
    }
}
