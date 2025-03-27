package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.IssueDTO;

@FunctionalInterface
public interface IssueUpdateListener {
    void onIssueUpdated(IssueDTO updatedProject);

}
