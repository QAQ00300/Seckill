package com.seckill.common.base.ao.req;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

public class BasePageQueryREQ implements Serializable {

    @Schema(description = "页码")
    protected int page;

    @Schema(description = "每页记录数")
    protected int pageSize;
}
