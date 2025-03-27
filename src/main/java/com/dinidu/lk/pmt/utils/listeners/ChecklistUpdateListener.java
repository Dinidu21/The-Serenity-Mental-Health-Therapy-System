package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.ChecklistDTO;

@FunctionalInterface
public interface ChecklistUpdateListener {
    void onChecklistUpdated(ChecklistDTO updatedChecklist);
}
