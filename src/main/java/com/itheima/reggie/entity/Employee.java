package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
//@Data ---lombok注解，自动生成getter/setter方法
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //创建人id----TableField注解：表示该字段在表中不存在，但是需要填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    //修改人id
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
