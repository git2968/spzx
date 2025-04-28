package com.spzx.product.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CategoryExcelVo {

    @ExcelProperty(value = "id" ,index = 0)
    private Long id;

    @ExcelProperty(value = "名称" ,index = 1)
    private String name;

    @ExcelProperty(value = "图标地址" ,index = 2)
    private String imageUrl ;

    @ExcelProperty(value = "上级id" ,index = 3)
    private Long parentId;

    @ExcelProperty(value = "是否显示" ,index = 4)
    private String status;

    @ExcelProperty(value = "排序" ,index = 5)
    private Long orderNum;

}