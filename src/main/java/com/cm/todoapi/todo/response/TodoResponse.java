package com.cm.todoapi.todo.response;

public class TodoResponse {
    Object success;
    Object error;

    public TodoResponse(Object success, Object error) {
        this.success = success;
        this.error = error;
    }

    @Override
    public String toString() {
        return "TodoResponse{" +
                "success=" + success +
                ", error=" + error +
                '}';
    }

    public Object getSuccess() {
        return success;
    }

    public void setSuccess(Object success) {
        this.success = success;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
