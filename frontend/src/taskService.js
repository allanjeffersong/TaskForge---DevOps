const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const handleResponse = async (response) => {
    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Erro desconhecido' }));
        throw new Error(error.message || `HTTP ${response.status}`);
    }
    if (response.status === 204) return null;
    return response.json();
};

export const taskService = {
    getAll: (params = {}) => {
        const query = new URLSearchParams();
        if (params.status) query.append('status', params.status);
        if (params.priority) query.append('priority', params.priority);
        return fetch(`${API_URL}/tasks?${query}`).then(handleResponse);
    },

    getById: (id) =>
        fetch(`${API_URL}/tasks/${id}`).then(handleResponse),

    create: (data) =>
        fetch(`${API_URL}/tasks`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        }).then(handleResponse),

    update: (id, data) =>
        fetch(`${API_URL}/tasks/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        }).then(handleResponse),

    delete: (id) =>
        fetch(`${API_URL}/tasks/${id}`, { method: 'DELETE' }).then(handleResponse),
};