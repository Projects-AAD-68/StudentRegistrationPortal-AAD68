package lk.ijse.gdse.aad68.studentmanagementportal.bo;

import lk.ijse.gdse.aad68.studentmanagementportal.dto.StudentDTO;

import java.sql.Connection;

public interface StudentBO {
    String saveStudent(StudentDTO student, Connection connection)throws Exception;
    boolean deleteStudent(String id, Connection connection)throws Exception;
    boolean updateStudent(String id,StudentDTO student,Connection connection)throws Exception;
    StudentDTO getStudent(String id,Connection connection)throws Exception;
}
