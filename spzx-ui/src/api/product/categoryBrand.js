import request from '@/utils/request'
// 查询分类品牌列表
export function listCategoryBrand(query) {
  return request({
    url: '/product/categoryBrand/list',
    method: 'get',
    params: query
  })
}

// 新增分类品牌
export function addCategoryBrand(data) {
    return request({
      url: '/product/categoryBrand',
      method: 'post',
      data: data
    })
  }

// 查询分类品牌详细
export function getCategoryBrand(id) {
    return request({
      url: '/product/categoryBrand/' + id,
      method: 'get'
    })
  }
  
  // 修改分类品牌
  export function updateCategoryBrand(data) {
    return request({
      url: '/product/categoryBrand',
      method: 'put',
      data: data
    })
  }

  export function delCategoryBrand(id) {
    return request({
      url: '/product/categoryBrand/' + id,
      method: 'delete'
    })
  }

  // 根据分类获取分类品牌
export function getCategoryBrandAll(categoryId) {
    return request({
      url: '/product/categoryBrand/brandList/' + categoryId,
      method: 'get'
    })
  }

  