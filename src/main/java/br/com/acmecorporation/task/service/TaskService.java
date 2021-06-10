package br.com.acmecorporation.task.service;

import br.com.acmecorporation.task.domain.Task;
import br.com.acmecorporation.task.domain.TaskStatus;
import br.com.acmecorporation.task.repository.TaskRepository;
import br.com.acmecorporation.user.domain.User;
import jdk.jshell.Snippet;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
public class TaskService {

    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> findAllBy(User user, TaskStatus status) {
        return taskRepository.findAllByUserAndStatusInOrderByStatusDesc(user, getStatus(status));
    }

    private List<TaskStatus> getStatus(TaskStatus status) {
        return status == null ? Arrays.asList(TaskStatus.values()) : Arrays.asList(status);
    }

    public Task findBy(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user);
    }

    public Task changeTaskStatus(Long id, TaskStatus status, User user) {
        Task taskToComplete = findBy(id, user);
        taskToComplete.setStatus(status);
        return save(taskToComplete);
    }
}
