import ApiService from '@/api'

const BASE_URL = '/paths'

const PathService = {
  get(params) {
    return ApiService.get(`${BASE_URL}`, params);
  }
}

export default PathService
