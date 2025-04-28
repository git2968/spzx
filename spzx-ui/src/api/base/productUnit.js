import request from '@/utils/request'

// 查询商品单位列表
export function listProductUnit(query) {
  return request({
    url: '/product/productUnit/list',
    method: 'get',
    params: query
  })
}

// 新增商品单位
export function addProductUnit(data) {
    return request({
      url: '/product/productUnit',
      method: 'post',
      data: data
    })
  }

  // 查询商品单位详细
export function getProductUnit(id) {
    return request({
      url: '/product/productUnit/' + id,
      method: 'get'
    })
  }
  
  // 修改商品单位
    export function updateProductUnit(data) {
    return request({
      url: '/product/productUnit',
      method: 'put',
      data: data
    })
  }

  // 删除商品单位
export function delProductUnit(id) {
    return request({
      url: '/product/productUnit/' + id,
      method: 'delete'
    })
  }

  // 查询全部商品单位
export function getUnitAll() {
    return request({
      url: '/product/productUnit/getUnitAll',
      method: 'get'
    })
  }