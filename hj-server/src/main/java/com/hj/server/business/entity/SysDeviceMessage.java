package com.hj.server.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_device_message")
public class SysDeviceMessage {
    @TableId(type = IdType.AUTO)
    private String id;

    private String content;

    private String contentJson;

    private String mn;

    private String flag;


}
