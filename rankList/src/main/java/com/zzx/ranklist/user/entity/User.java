package com.zzx.ranklist.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {

    @TableId(value = "id")
    private String id;

    @TableField(value = "name")
    private String name;

    @TableField(exist = false)
    private Double likeCount;
}
