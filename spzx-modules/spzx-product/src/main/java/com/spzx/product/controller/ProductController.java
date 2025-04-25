package com.spzx.product.controller;

import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.Product;
import com.spzx.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.core.web.controller.BaseController;

import java.util.List;

/**
 * <p>
 * 商品 前端控制器
 * </p>
 *
 * @author Johnny
 * @since 2025-04-21
 */
/**
 * 商品Controller
 */
@Tag(name = "商品接口管理")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private IProductService productService;

    /**
     * 查询商品列表
     */
    @Operation(summary = "查询商品列表")
    @GetMapping("/list")
    public TableDataInfo list(Product product) {
        startPage();
        List<Product> list = productService.selectProductList(product);
        return getDataTable(list);
    }
    @Operation(summary = "商品审核")
    @GetMapping("updateAuditStatus/{id}/{auditStatus}")
    public AjaxResult updateAuditStatus(@PathVariable Long id, @PathVariable Integer auditStatus) {

        productService.updateAuditStatus(id, auditStatus);
        return success();
    }
    @Operation(summary = "更新上下架状态")
    @GetMapping("updateStatus/{id}/{status}")
    public AjaxResult updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        productService.updateStatus(id, status);
        return success();
    }
    @Operation(summary = "新增商品")
    @PostMapping
    public AjaxResult add(@RequestBody Product product) {
        return toAjax(productService.insertProduct(product));
    }

    @Operation(summary = "获取商品详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productService.selectProductById(id));
    }

    @Operation(summary = "修改商品")
    @PutMapping
    public AjaxResult edit(@RequestBody Product product) {
        return toAjax(productService.updateProduct(product));
    }
    @Operation(summary = "删除商品")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(productService.deleteProductByIds(ids));
    }


}
