package com.spzx.product.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.spzx.common.core.web.domain.BaseEntity;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品sku属性表
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Getter
@Setter
@TableName("product_details")
public class ProductDetails extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 详情图片地址
     */
    private String imageUrls;
}
