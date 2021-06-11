package br.com.acmecorporation.task.exception;

public class TaskNotFoundException extends Exception {

    public TaskNotFoundException(String body) {
        super(body);
    }
}
