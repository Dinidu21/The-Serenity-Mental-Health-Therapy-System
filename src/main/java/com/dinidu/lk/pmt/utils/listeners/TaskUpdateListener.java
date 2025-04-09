package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;

@FunctionalInterface
public interface TaskUpdateListener {
    void onTaskUpdated(TherapyProgramsDTO updatedTask);
}