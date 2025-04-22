package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品sku库存表
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Getter
@Setter
@TableName("sku_stock")
public class SkuStock extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long skuId;

    /**
     * 总库存数
     */
    private Integer totalNum;

    /**
     * 锁定库存
     */
    private Integer lockNum;

    /**
     * 可用库存
     */
    private Integer availableNum;

    /**
     * 销量
     */
    private Integer saleNum;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    private Byte status;
}
