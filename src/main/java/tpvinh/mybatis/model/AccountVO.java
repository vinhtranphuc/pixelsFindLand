package tpvinh.mybatis.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AccountVO {

    private Long id;
    private String roleName;

    private String loginId;
    @JsonIgnore
    private String password;
    private String phone;

    private boolean enabled;
    private boolean deleted;

    private Date createdDt;
    private Long createdId;

    private Date updatedDt;
    private String updatedId;

    private Date deletedDt;
    private Long deletedId;
}
