package com.dinidu.lk.pmt.dao;

import java.sql.*;
import java.util.*;

public interface CrudDAO<T> extends SuperDAO {
    boolean save(T dto) throws SQLException,ClassNotFoundException;
    boolean update(T dto) throws SQLException,ClassNotFoundException;
    boolean insert(T t) throws SQLException,ClassNotFoundException;
    boolean delete(String idOrName)throws SQLException,ClassNotFoundException;
    List<T> fetchAll() throws SQLException,ClassNotFoundException;
    Map<String, String> getAllNames() throws SQLException,ClassNotFoundException;
    List<T> searchByName(String searchQuery) throws SQLException,ClassNotFoundException;
    Long getIdByName(String taskName) throws SQLException,ClassNotFoundException;
}
