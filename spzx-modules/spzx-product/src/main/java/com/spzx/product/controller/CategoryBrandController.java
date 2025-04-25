package com.spzx.product.controller;

import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Category;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.ICategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.core.web.controller.BaseController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 分类品牌 前端控制器
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
@Tag(name = "分类品牌接口管理")
@RestController
@RequestMapping("/categoryBrand")
public class CategoryBrandController extends BaseController {

    @Autowired
    private ICategoryBrandService categoryBrandService;

    /**
     * 查询分类品牌列表
     */
    @Operation(summary = "查询分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(CategoryBrand categoryBrand) {
        startPage();
        List<CategoryBrand> list = categoryBrandService.selectCategoryBrandList(categoryBrand);
        return getDataTable(list);
    }

    /**
     * 获取分类品牌详细信息
     */
    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        CategoryBrand categoryBrand = categoryBrandService.selectCategoryBrandById(id);
        return success(categoryBrand);
    }

    /**
     * 新增分类品牌
     */
    @Operation(summary = "新增分类品牌")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated CategoryBrand categoryBrand) {
        categoryBrand.setCreateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.insertCategoryBrand(categoryBrand));
    }

    /**
     * 修改分类品牌
     */
    @Operation(summary = "修改分类品牌")
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated CategoryBrand categoryBrand) {
        categoryBrand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.updateCategoryBrand(categoryBrand));
    }

    /**
     * 删除分类品牌
     */
    @Operation(summary = "删除分类品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(categoryBrandService.removeBatchByIds(Arrays.asList(ids)));
    }
    /**
     * 根据分类id获取品牌列表
     * @param categoryId
     * @return
     */
    @Operation(summary = "根据分类id获取品牌列表")
    @GetMapping("brandList/{categoryId}")
    public AjaxResult selectBrandListByCategoryId(@PathVariable Long categoryId) {
        return success(categoryBrandService.selectBrandListByCategoryId(categoryId));
    }


}
