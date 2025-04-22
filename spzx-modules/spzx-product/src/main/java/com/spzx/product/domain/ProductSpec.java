package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品规格
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Getter
@Setter
@TableName("product_spec")
public class ProductSpec extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 规格值："[{"key":"颜色","valueList":["蓝","白","红"]]"
     */
    private String specValue;
}
