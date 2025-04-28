import request from '@/utils/request'

// 查询商品规格列表
export function listSpec(query) {
  return request({
    url: '/product/productSpec/list',
    method: 'get',
    params: query
  })
}
// 新增商品规格
export function addSpec(data) {
    return request({
      url: '/product/productSpec',
      method: 'post',
      data: data
    })
  }

  // 查询商品规格详细
export function getSpec(id) {
    return request({
      url: '/product/productSpec/' + id,
      method: 'get'
    })
  }
  
  // 修改商品规格
  export function updateSpec(data) {
    return request({
      url: '/product/productSpec',
      method: 'put',
      data: data
    })
  }
  // 删除商品规格
export function delSpec(id) {
    return request({
      url: '/product/productSpec/' + id,
      method: 'delete'
    })
  }

  // 根据分类获取分类规格
export function getCategorySpecAll(categoryId) {
    return request({
      url: '/product/productSpec/productSpecList/' + categoryId,
      method: 'get'
    })
  }