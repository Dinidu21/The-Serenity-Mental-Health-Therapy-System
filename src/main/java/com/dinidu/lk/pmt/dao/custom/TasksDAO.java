package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Tasks;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;

import java.sql.SQLException;
import java.util.List;

public interface TasksDAO extends CrudDAO<Tasks> {
    String getTaskNameById(Long taskId) throws SQLException, ClassNotFoundException;
    List<Tasks> getByStatus(TaskStatus taskStatus) throws SQLException, ClassNotFoundException;
    List<Tasks> getTasksCurrentProjectByStatus(String projectId, TaskStatus taskStatus) throws SQLException, ClassNotFoundException;
    List<Tasks> getTaskByProjectId(String id) throws SQLException, ClassNotFoundException;
    void updateTask(Tasks currentTask) throws SQLException, ClassNotFoundException;
    boolean insertTask(Tasks task) throws SQLException, ClassNotFoundException;
    long getLastInsertedTaskId() throws SQLException, ClassNotFoundException;
    boolean insertAssignment(long lastInsertedTaskId, Long aLong) throws SQLException, ClassNotFoundException;
}
