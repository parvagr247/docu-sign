import api from "../api/axios";
import { getToken } from "../utils/token";

export const getDocumentSigners = async (documentId) => {

  const response = await api.get(
    `/documents/${documentId}/signers`,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};

export const createSigner = async (
  documentId,
  signerData
) => {

  const response = await api.post(
    `/documents/${documentId}/signers`,
    signerData,
    {
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  return response.data;
};