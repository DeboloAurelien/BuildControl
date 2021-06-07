package fr.epsi.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface Repository<T, ID> {
    T findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;
    T create(T t) throws SQLException;
    T update(T t) throws SQLException;
    void delete(T t) throws SQLException;
    T setData(ResultSet rs) throws SQLException;
}
