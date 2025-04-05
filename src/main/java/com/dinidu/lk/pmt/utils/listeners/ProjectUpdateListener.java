package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.TherapistDTO;
@FunctionalInterface
public interface ProjectUpdateListener {
    void onProjectUpdated(TherapistDTO updatedProject);
}

