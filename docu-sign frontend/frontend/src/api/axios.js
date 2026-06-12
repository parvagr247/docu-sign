import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:9099/api",
});

export default api;