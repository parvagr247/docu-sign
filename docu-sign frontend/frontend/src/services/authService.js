import api from "../api/axios";

export const login = async (email, password) => {
  const response = await api.post("/auth/login", {
    email,
    password,
  });

  return response.data;
};

export const register = async (fullName, email, password) => {
  const response = await api.post("/auth/register", {
    fullName,
    email,
    password,
  });

  return response.data;
};