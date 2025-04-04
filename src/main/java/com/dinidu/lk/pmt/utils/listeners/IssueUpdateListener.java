package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.PatientsDTO;

@FunctionalInterface
public interface IssueUpdateListener {
    void onIssueUpdated(PatientsDTO updatedProject);

}
