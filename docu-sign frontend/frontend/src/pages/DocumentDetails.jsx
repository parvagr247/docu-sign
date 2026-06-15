import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import {
    getDocumentById,
    sendSignatureRequest,
    downloadDocumentBlob
}
    from "../services/documentService";

import { getDocumentSigners }
    from "../services/signerService";

import { getSignatureFields }
    from "../services/signatureFieldService";

import AddSignerForm
    from "../components/AddSignerForm";

import PdfViewer
from "../components/PdfViewer";

import SignerSelector
  from "../components/SignerSelector";




function DocumentDetails() {

    const { id } = useParams();

    const [document, setDocument] = useState(null);
    const [signers, setSigners] = useState([]);
    const [fields, setFields] = useState([]);

    const [loading, setLoading] = useState(true);

    const [sending, setSending] = useState(false);

    const [pdfBlob, setPdfBlob] = useState(null);

    const [
  selectedSignerId,
  setSelectedSignerId
] = useState("");



    const handleSendRequest =
        async () => {

            try {

                setSending(true);

                await sendSignatureRequest(id);

                const updatedDocument =
                    await getDocumentById(id);

                setDocument(updatedDocument);

                alert(
                    "Signature request sent successfully"
                );

            } catch (error) {

                console.error(error);

                alert(
                    error.response?.data?.message
                    || "Failed to send request"
                );

            } finally {

                setSending(false);

            }
        };

    const loadSigners = async () => {

        try {

            const signerData =
                await getDocumentSigners(id);

            setSigners(signerData);

        } catch (error) {

            console.error(error);

        }
    };

    const loadFields =
  async () => {

    try {

      const data =
        await getSignatureFields(id);

      setFields(data);

    } catch (error) {

      console.error(error);

    }
};

    useEffect(() => {

        const loadData = async () => {

            try {

                const [
  documentData,
  signerData,
  fieldData,
  pdfBlobData
] = await Promise.all([
  getDocumentById(id),
  getDocumentSigners(id),
  getSignatureFields(id),
  downloadDocumentBlob(id)
]);

                setDocument(documentData);
                setSigners(signerData);
                setFields(fieldData);
                setPdfBlob(pdfBlobData);

            } catch (error) {

                console.error(error);

            } finally {

                setLoading(false);

            }
        };

        loadData();

    }, [id]);

    if (loading) {
        return <h2>Loading...</h2>;
    }

    if (!document) {
        return <h2>Document not found</h2>;
    }

    return (
        <div>

            <Link to="/documents">
                ← Back to Documents
            </Link>

            <h1>Document Details</h1>

            <hr />

            <h2>Document Information</h2>

            <hr />

            <h2>PDF Preview</h2>

<SignerSelector
  signers={signers}
  selectedSignerId={selectedSignerId}
  onChange={setSelectedSignerId}
/>

<PdfViewer
  pdfBlob={pdfBlob}
  documentId={id}
  selectedSignerId={selectedSignerId}
  fields={fields}
  onFieldCreated={loadFields}
  documentStatus={document.status}
  signers={signers}
/>

            <hr />

            <p>
                <strong>Name:</strong>
                {" "}
                {document.originalFileName}
            </p>

            <p>
                <strong>Status:</strong>
                {" "}
                {document.status}
            </p>

            <p>
                <strong>File Size:</strong>
                {" "}
                {document.fileSize}
                {" "}
                bytes
            </p>

            <p>
                <strong>Uploaded At:</strong>
                {" "}
                {document.uploadedAt}
            </p>

            <hr />

            <hr />

            <AddSignerForm
                documentId={id}
                onSignerAdded={loadSigners}
            />

            <hr />

            <h2>Signers</h2>

            {signers.length === 0 ? (
                <p>No signers added.</p>
            ) : (
                signers.map((signer) => (
                    <div
                        key={signer.id}
                        style={{
                            border: "1px solid #ccc",
                            padding: "10px",
                            marginBottom: "10px"
                        }}
                    >
                        <p>
                            <strong>Name:</strong>
                            {" "}
                            {signer.name}
                        </p>

                        <p>
                            <strong>Email:</strong>
                            {" "}
                            {signer.email}
                        </p>

                        <p>
                            <strong>Status:</strong>
                            {" "}
                            {signer.status}
                        </p>
                    </div>
                ))
            )}

            <hr />

            <h2>Signature Fields</h2>

            {fields.length === 0 ? (
                <p>No signature fields configured.</p>
            ) : (
                fields.map((field) => (
                    <div
                        key={field.id}
                        style={{
                            border: "1px solid #ccc",
                            padding: "10px",
                            marginBottom: "10px"
                        }}
                    >
                        <p>
                            <strong>Page:</strong>
                            {" "}
                            {field.pageNumber}
                        </p>

                        <p>
                            <strong>X:</strong>
                            {" "}
                            {field.xPosition}
                        </p>

                        <p>
                            <strong>Y:</strong>
                            {" "}
                            {field.yPosition}
                        </p>

                        <p>
                            <strong>Width:</strong>
                            {" "}
                            {field.width}
                        </p>

                        <p>
                            <strong>Height:</strong>
                            {" "}
                            {field.height}
                        </p>

                        <p>
                            <strong>Required:</strong>
                            {" "}
                            {field.required
                                ? "Yes"
                                : "No"}
                        </p>

                    </div>
                ))
            )}

            <hr />

            <h2>Actions</h2>

            <button
                onClick={handleSendRequest}
                disabled={
                    sending ||
                    document.status !== "UPLOADED"
                }
            >
                {
                    sending
                        ? "Sending..."
                        : "Send Signature Request"
                }
            </button>

            <button
  onClick={async () => {

    try {

      const blob =
        await downloadDocumentBlob(id);

      console.log(blob);

    } catch (error) {

      console.log("FULL ERROR");
      console.log(error);

      console.log("RESPONSE");
      console.log(error.response);

      console.log("DATA");
      console.log(error.response?.data);

    }

  }}
>
  Test Blob Download
</button>

            <hr />



        </div>
    );
}

export default DocumentDetails;