package com.spzx.product.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.spzx.common.core.constant.HttpStatus;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.utils.bean.BeanUtils;
import com.spzx.common.core.utils.poi.ExcelUtil;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.product.api.domain.vo.CategoryVo;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.listener.ExcelListener;
import com.spzx.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.core.web.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品分类 前端控制器
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Tag(name = "商品分类")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Autowired
    private ICategoryService categoryService;
    @Operation(summary = "获取分类下拉树列表")
    @GetMapping("/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable Long id){
        List<Category> categoryList = categoryService.treeSelect(id);
        return success(categoryList);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        ExcelUtil<Category> util = new ExcelUtil<>(Category.class);
        util.importTemplateExcel(response, "用户数据");
    }
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelListener<CategoryExcelVo> categoryExcelDataListener = new ExcelListener<CategoryExcelVo>();
        EasyExcel.read(file.getInputStream())
                .head(CategoryExcelVo.class)
                .sheet()
                .registerReadListener(categoryExcelDataListener)
                .doRead();

        List<CategoryExcelVo> datas = categoryExcelDataListener.getDatas();
        if(!CollectionUtils.isEmpty(datas)) {
            List<Category> categoryList = new ArrayList<>(datas.size());
            for(CategoryExcelVo categoryExcelVo : datas) {
                Category category = new Category();
                BeanUtils.copyProperties(categoryExcelVo, category, Category.class);
                category.setStatus("显示".equals(categoryExcelVo.getStatus()) ? 1 : 0);
                categoryList.add(category);
            }
            categoryService.saveBatch(categoryList);
        }

        return success();
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("商品分类", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            List<CategoryExcelVo> list = categoryService.list()
                    .stream().map(category -> {
                        CategoryExcelVo categoryExcelVo= new CategoryExcelVo();
                        BeanUtils.copyProperties(category , categoryExcelVo);
                        categoryExcelVo.setStatus(category.getStatus() == 1 ? "显示" : "不显示");
                        return categoryExcelVo;
                    }).collect(Collectors.toList());

            EasyExcel.write(response.getOutputStream(), CategoryExcelVo.class).sheet("模板").doWrite(list);
        } catch (IOException e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, Object> map = MapUtils.newHashMap();
            map.put("code", HttpStatus.ERROR);
            map.put("msg", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }
    @InnerAuth
    @GetMapping(value = "/getOneCategory")
    public R<List<CategoryVo> > getOneCategory() {
        return R.ok(categoryService.getOneCategory());
    }

    @InnerAuth
    @GetMapping(value = "/tree")
    public R<List<CategoryVo> > tree() {
        return R.ok(categoryService.tree());
    }


}
