import api from "../api/axios";
import { getToken } from "../utils/token";

export const getDocuments = async () => {

  const response = await api.get(
    "/documents",
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};

export const uploadDocument = async (file) => {

  const formData = new FormData();

  formData.append("file", file);

  const response = await api.post(
    "/documents/upload",
    formData,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};

export const getDocumentById = async (id) => {

  const response = await api.get(
    `/documents/${id}`,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};

export const sendSignatureRequest =
  async (documentId) => {

    const response = await api.post(
      `/documents/${documentId}/send`,
      {},
      {
        headers: {
          Authorization:
            `Bearer ${getToken()}`
        }
      }
    );

    return response.data;
};

export const getDocumentDownloadUrl =
  (documentId) => {

    return `${import.meta.env.VITE_API_URL}/documents/${documentId}/download`;
};

export const downloadDocumentBlob = async (
  documentId
) => {

  const response = await api.get(
    `/documents/${documentId}/download`,
    {
      responseType: "blob",

      headers: {
        Authorization:
          `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};