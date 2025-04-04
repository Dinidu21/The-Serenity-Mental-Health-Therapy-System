package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.ProgramsDTO;

@FunctionalInterface
public interface TaskUpdateListener {
    void onTaskUpdated(ProgramsDTO updatedTask);
}