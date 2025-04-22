package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品sku
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Getter
@Setter
@TableName("product_sku")
public class ProductSku extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品编号
     */
    private String skuCode;

    private String skuName;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 缩略图路径
     */
    private String thumbImg;

    /**
     * 售价
     */
    private BigDecimal salePrice;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * sku规格信息json
     */
    private String skuSpec;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 体积
     */
    private BigDecimal volume;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    private Byte status;
}
