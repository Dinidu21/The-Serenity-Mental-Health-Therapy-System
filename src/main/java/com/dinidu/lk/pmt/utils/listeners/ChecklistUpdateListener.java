package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.TherapySessionsDTO;

@FunctionalInterface
public interface ChecklistUpdateListener {
    void onChecklistUpdated(TherapySessionsDTO updatedChecklist);
}
