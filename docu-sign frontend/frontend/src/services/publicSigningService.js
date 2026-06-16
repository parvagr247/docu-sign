import api from "../api/axios";

export const getSigningSession = async (token) => {
  const response = await api.get(`/public/sign/${token}`);
  return response.data;
};

export const submitSignature = async (
  token,
  signatureBlob
) => {

  const formData = new FormData();

  formData.append(
    "signatureImage",
    signatureBlob,
    "signature.png"
  );

  const response = await api.post(
    `/public/sign/${token}`,
    formData
  );

  return response.data;
};