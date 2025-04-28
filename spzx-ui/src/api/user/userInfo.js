import request from '@/utils/request'

// 查询会员列表
export function listUserInfo(query) {
  return request({
    url: '/user/userInfo/list',
    method: 'get',
    params: query
  })
}

// 查询会员详细
export function getUserInfo(id) {
  return request({
    url: '/user/userInfo/' + id,
    method: 'get'
  })
}

// 新增会员
export function addUserInfo(data) {
  return request({
    url: '/user/userInfo',
    method: 'post',
    data: data
  })
}

// 修改会员
export function updateUserInfo(data) {
  return request({
    url: '/user/userInfo',
    method: 'put',
    data: data
  })
}

// 删除会员
export function delUserInfo(id) {
  return request({
    url: '/user/userInfo/' + id,
    method: 'delete'
  })
}

// 获取会员地址
export function getUserAddress(id) {
  return request({
    url: '/user/userInfo/getUserAddress/' + id,
    method: 'get'
  })
}