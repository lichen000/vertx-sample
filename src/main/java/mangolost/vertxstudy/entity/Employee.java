package mangolost.vertxstudy.entity;

import io.vertx.ext.sql.ResultSet;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by chen.li200 on 2018-03-19
 */
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id; //主键id
    private Timestamp create_time; //记录创建时间
    private Timestamp update_time; //记录更新时间
    private Integer record_status; //记录状态
    private String employee_no; //工号
    private String employee_name;//姓名
    private Integer age; //年龄

    public Employee() {

    }

    public Employee(ResultSet rs) {
        this.id = rs.getNumRows();
        this.employee_no = rs.getColumnNames().get(0);
    }

    public Employee(Integer id, Timestamp create_time, Timestamp update_time, Integer record_status, String employee_no, String employee_name, Integer age) {
        this.id = id;
        this.create_time = create_time;
        this.update_time = update_time;
        this.record_status = record_status;
        this.employee_no = employee_no;
        this.employee_name = employee_name;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public Integer getRecord_status() {
        return record_status;
    }

    public void setRecord_status(Integer record_status) {
        this.record_status = record_status;
    }

    public String getEmployee_no() {
        return employee_no;
    }

    public void setEmployee_no(String employee_no) {
        this.employee_no = employee_no;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}