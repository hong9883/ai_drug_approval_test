import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Document APIs
export const documentAPI = {
  upload: (formData) => {
    return api.post('/documents', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  getList: (params) => {
    return api.get('/documents', { params });
  },

  search: (keyword, params) => {
    return api.get('/documents/search', {
      params: { keyword, ...params }
    });
  },

  getById: (id) => {
    return api.get(`/documents/${id}`);
  },

  download: (id) => {
    return api.get(`/documents/${id}/download`, {
      responseType: 'blob',
    });
  },

  delete: (id) => {
    return api.delete(`/documents/${id}`);
  },
};

// Query APIs
export const queryAPI = {
  ask: (data) => {
    return api.post('/queries', data);
  },

  getHistory: (params) => {
    return api.get('/queries/history', { params });
  },

  getUserHistory: (userName, params) => {
    return api.get(`/queries/history/user/${userName}`, { params });
  },

  getDetail: (id) => {
    return api.get(`/queries/history/${id}`);
  },
};

// Statistics APIs
export const statisticsAPI = {
  getAll: () => {
    return api.get('/statistics');
  },

  getDocuments: () => {
    return api.get('/statistics/documents');
  },

  getQueries: () => {
    return api.get('/statistics/queries');
  },
};

export default api;
