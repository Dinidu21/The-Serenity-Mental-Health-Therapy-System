package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.TasksDTO;

@FunctionalInterface
public interface TaskUpdateListener {
    void onTaskUpdated(TasksDTO updatedTask);
}