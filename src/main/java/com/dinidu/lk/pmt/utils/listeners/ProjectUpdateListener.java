package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.ProjectDTO;
@FunctionalInterface
public interface ProjectUpdateListener {
    void onProjectUpdated(ProjectDTO updatedProject);
}

