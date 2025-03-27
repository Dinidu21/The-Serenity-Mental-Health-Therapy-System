package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.TasksDTO;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;

import java.sql.SQLException;
import java.util.List;

public interface TasksBO extends SuperBO {
    List<TasksDTO> searchTasksByName(String searchQuery) throws SQLException, ClassNotFoundException;
    List<TasksDTO> getAllTasks() throws SQLException, ClassNotFoundException;
    List<TasksDTO> getTaskByProjectId(String id) throws SQLException, ClassNotFoundException;
    String getTaskNameById(Long taskId) throws SQLException, ClassNotFoundException;
    void updateTask(TasksDTO currentTask) throws SQLException, ClassNotFoundException;
    boolean deleteTask(String name) throws SQLException, ClassNotFoundException;
    List<TasksDTO> getTasksByStatus(TaskStatus taskStatus) throws SQLException , ClassNotFoundException;
    List<TasksDTO> getTasksCurrentProjectByStatus(String projectId, TaskStatus taskStatus) throws SQLException , ClassNotFoundException;
    boolean insertTask(TasksDTO tasksDTO) throws SQLException , ClassNotFoundException;
}
