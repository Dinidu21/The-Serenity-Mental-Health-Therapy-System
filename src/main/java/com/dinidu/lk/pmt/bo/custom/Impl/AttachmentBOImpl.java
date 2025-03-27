package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.IssueAttachmentBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.IssueAttachmentDAO;
import com.dinidu.lk.pmt.dto.IssueAttachmentDTO;
import com.dinidu.lk.pmt.entity.IssueAttachment;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;


import java.sql.SQLException;
import java.util.List;

public class AttachmentBOImpl implements IssueAttachmentBO {
    IssueAttachmentDAO issueAttachmentDAO = (IssueAttachmentDAO)
            DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ATTACHMENTS);


    @Override
    public void saveAttachment(IssueAttachmentDTO attachment) throws SQLException, ClassNotFoundException {
        issueAttachmentDAO.saveAttachment(EntityDTOMapper.mapDTOToEntity(attachment, IssueAttachment.class));
    }

    @Override
    public boolean deleteAttachment(Long attachmentId) throws SQLException, ClassNotFoundException {
        return issueAttachmentDAO.deleteAttachment(attachmentId);
    }

    @Override
    public List<IssueAttachmentDTO> getAttachments(Long issueId) throws SQLException, ClassNotFoundException {
        return EntityDTOMapper.mapEntityListToDTOList(issueAttachmentDAO.getAttachments(issueId),
                IssueAttachmentDTO.class);
    }

    @Override
    public long getLastAddedAttachmentId(Long currentIssueId) throws SQLException, ClassNotFoundException {
        return issueAttachmentDAO.getLastAddedAttachmentId(currentIssueId);
    }
}
