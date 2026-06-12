export const saveAuthData = (data) => {
  localStorage.setItem("token", data.token);
  localStorage.setItem("email", data.email);
  localStorage.setItem("role", data.role);
};

export const getToken = () => {
  return localStorage.getItem("token");
};

export const getEmail = () => {
  return localStorage.getItem("email");
};

export const getRole = () => {
  return localStorage.getItem("role");
};

export const clearAuthData = () => {
  localStorage.clear();
};