import api from "../api/axios";
import { getToken } from "../utils/token";

export const getSignatureFields = async (documentId) => {

  const response = await api.get(
    `/documents/${documentId}/signature-fields`,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};