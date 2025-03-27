package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.IssueAttachmentDTO;

import java.sql.SQLException;
import java.util.List;

public interface IssueAttachmentBO extends SuperBO {
    void saveAttachment(IssueAttachmentDTO attachment) throws SQLException,ClassNotFoundException;
    boolean deleteAttachment(Long attachmentId) throws SQLException,ClassNotFoundException;
    List<IssueAttachmentDTO> getAttachments(Long issueId) throws SQLException,ClassNotFoundException;
    long getLastAddedAttachmentId(Long currentIssueId) throws SQLException,ClassNotFoundException;
}
