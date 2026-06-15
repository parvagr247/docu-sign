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

export const createSignatureField = async (
  documentId,
  fieldData
) => {

  const response = await api.post(
    `/documents/${documentId}/signature-fields`,
    fieldData,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};

export const deleteSignatureField = async (
  fieldId
) => {

  await api.delete(
    `/signature-fields/${fieldId}`,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );
};