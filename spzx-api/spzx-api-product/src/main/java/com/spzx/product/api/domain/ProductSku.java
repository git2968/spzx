package com.spzx.product.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品sku对象 product_sku
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Data
@TableName("product_sku")
public class ProductSku extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品编号
     */
    @Schema(description = "商品编号")
    private String skuCode;
    @Schema(description = "商品名称")
    private String skuName;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 缩略图路径
     */
    @Schema(description = "缩略图路径")
    private String thumbImg;

    /**
     * 售价
     */
    @Schema(description = "售价")
    private BigDecimal salePrice;

    /**
     * 市场价
     */
    @Schema(description = "市场价")
    private BigDecimal marketPrice;

    /**
     * 成本价
     */
    @Schema(description = "成本价")
    private BigDecimal costPrice;

    /**
     * sku规格信息json
     */
    @Schema(description = "sku规格信息json")
    private String skuSpec;

    /**
     * 重量
     */
    @Schema(description = "重量")
    private BigDecimal weight;

    /**
     * 体积
     */
    @Schema(description = "体积")
    private BigDecimal volume;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    @Schema(description = "线上状态：0-初始值，1-上架，-1-自主下架")
    private Byte status;

    @Schema(description = "sku库存")
    @TableField(exist = false)
    private Integer stockNum;

    @Schema(description = "sku销量")
    @TableField(exist = false)
    private Integer saleNum;


}
