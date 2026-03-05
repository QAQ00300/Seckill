package com.seckill.common.base.bo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;


@Data
@SuperBuilder
@NoArgsConstructor
public class BaseEO implements Serializable {

    @TableId(value = "id", type = IdType.ASSIGN_ID) //默认自增
    @TableField(value = "id", fill = FieldFill.INSERT)
    protected Long id;


}
