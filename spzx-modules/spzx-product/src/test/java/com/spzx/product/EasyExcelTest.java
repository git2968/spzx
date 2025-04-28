package com.spzx.product;

import com.alibaba.excel.EasyExcel;
import com.spzx.common.core.utils.bean.BeanUtils;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.listener.ExcelListener;
import com.spzx.product.service.ICategoryService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class EasyExcelTest {
    @Resource
    ICategoryService categoryService;

    @Test
    public void writeDataToExcel() {
        List<CategoryExcelVo> list = categoryService.list()
                .stream().map(category -> {
                    CategoryExcelVo categoryExcelVo= new CategoryExcelVo();
                    BeanUtils.copyProperties(category , categoryExcelVo);
                    categoryExcelVo.setStatus(category.getStatus() == 1 ? "显示" : "不显示");
                    return categoryExcelVo;
                }).collect(Collectors.toList());

        EasyExcel.write("D:\\分类数据-导出.xlsx" , CategoryExcelVo.class).sheet("分类数据").doWrite(list);
    }
    @Test
    public void easyExcelReadTest() {
        String fileName = "D:\\BaiduNetdiskDownload\\后端工程师之总结;笔记\\尚品甄选\\2.资料\\分类数据.xlsx" ;
        ExcelListener categoryExcelDataListener = new ExcelListener();
        EasyExcel.read(fileName)
                .head(CategoryExcelVo.class)
                .sheet()
                .registerReadListener(categoryExcelDataListener)
                .doRead();

        categoryExcelDataListener.getDatas().forEach(s -> System.out.println(s) );   // 进行遍历操作
        categoryExcelDataListener.getDatas().clear();
    }
}
