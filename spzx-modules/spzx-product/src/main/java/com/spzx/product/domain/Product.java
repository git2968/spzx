package com.spzx.product.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Getter
@Setter
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 一级分类id
     */
    private Long category1Id;

    /**
     * 二级分类id
     */
    private Long category2Id;

    /**
     * 三级分类id
     */
    private Long category3Id;

    /**
     * 计量单位
     */
    private String unitName;

    /**
     * 轮播图
     */
    private String sliderUrls;

    /**
     * 商品规格json
     */
    private String specValue;

    /**
     * 线上状态：0-初始值，1-上架，-1-自主下架
     */
    private Byte status;

    /**
     * 审核状态：0-初始值，1-通过，-1-未通过
     */
    private Byte auditStatus;

    /**
     * 审核信息
     */
    private String auditMessage;
}
