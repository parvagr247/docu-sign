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

export const completeField = async (
  token,
  fieldId
) => {

  const response = await api.post(
    `/public/sign/${token}/fields/${fieldId}/complete`
  );

  return response.data;
};

export const downloadSigningPdf =
  async (token) => {

    const response =
      await api.get(
        `/public/sign/${token}/document`,
        {
          responseType: "blob"
        }
      );

    return response.data;
};

export const saveSignature =
  async (
    token,
    signatureBlob
  ) => {

    const formData =
      new FormData();

    formData.append(
      "signatureImage",
      signatureBlob,
      "signature.png"
    );

    const response =
      await api.post(
        `/public/sign/${token}/signature`,
        formData
      );

    return response.data;
};

export const completeSigning =
  async (token) => {

    const response =
      await api.post(
        `/public/sign/${token}/complete`
      );

    return response.data;
};