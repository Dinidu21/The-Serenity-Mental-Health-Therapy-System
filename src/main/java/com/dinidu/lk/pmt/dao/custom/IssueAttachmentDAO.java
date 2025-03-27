package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;

import java.sql.SQLException;
import java.util.List;

public interface IssueAttachmentDAO extends CrudDAO<IssueAttachment> {
    boolean deleteAttachment(Long attachmentId)throws SQLException,ClassNotFoundException;
    List<IssueAttachment> getAttachments(Long issueId) throws SQLException,ClassNotFoundException;
    void saveAttachment(IssueAttachment attachment) throws SQLException ,ClassNotFoundException;
    long getLastAddedAttachmentId(Long currentIssueId) throws SQLException, ClassNotFoundException;
}
