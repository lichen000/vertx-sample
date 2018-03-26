package mangolost.vertxstudy.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by chen.li200 on 2018-03-19
 */
public class Student implements Serializable {

    public static final long serialVersionUID = 1L;

    private Integer id; //主键id
    private Timestamp create_time; //记录创建时间
    private Timestamp update_time; //记录更新时间
    private String note; //备注

    private String number; //学号
    private String name;//姓名
    private Integer age; //年龄

    public Student() {
        super();
    }

    public Student(Integer id, Timestamp create_time, Timestamp update_time, String note, String number, String name, Integer age) {
        super();
        this.id = id;
        this.create_time = create_time;
        this.update_time = update_time;
        this.note = note;
        this.number = number;
        this.name = name;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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