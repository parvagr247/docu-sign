import axios from "axios";

export const API_BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:9099";

const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
});

export default api;