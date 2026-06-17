import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import "./styles/DocumentDetails.css";
import Layout from "../components/Layout";

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

    const [
        placementMode,
        setPlacementMode
    ] = useState(false);



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

    const loadFields = async () => {

        try {

            const data =
                await getSignatureFields(id);

            console.log(
                "FIELDS FROM SERVER",
                JSON.stringify(data, null, 2)
            );

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

    console.log(
        "DocumentDetails placementMode:",
        placementMode
    );

    return (
        <Layout>
            <div className="document-details">

                <div className="document-layout">

                    <div className="pdf-section">

                        <div className="pdf-header">

    <h2>Document Preview</h2>

    <SignerSelector
        signers={signers}
        selectedSignerId={selectedSignerId}
        onChange={setSelectedSignerId}
    />

</div>

                        <div className="pdf-container">

                            <PdfViewer
                                pdfBlob={pdfBlob}
                                documentId={id}
                                selectedSignerId={selectedSignerId}
                                fields={fields}
                                onFieldCreated={loadFields}
                                documentStatus={document.status}
                                signers={signers}
                                placementMode={placementMode}
                            />

                        </div>

                    </div>

                    <div className="sidebar-section">

                        <div className="sidebar-card">

                            <div className="document-summary">

  <div className="document-file-name">
    📄 {document.originalFileName}
  </div>

  <span
    className={`status-badge status-${document.status.toLowerCase()}`}
  >
    {document.status}
  </span>

  <div className="upload-date">
    Uploaded on {" "}
    {new Date(document.uploadedAt)
      .toLocaleDateString()}
  </div>

</div>

                            <div className="overview-grid">

                                <div className="overview-item">
                                    <span>File Size</span>
                                    <h3>
                                        {Math.round(document.fileSize / 1024)}
                                        {" "}KB
                                    </h3>
                                </div>

                                <div className="overview-item">
                                    <span>Signers</span>
                                    <h3>{signers.length}</h3>
                                </div>

                                <div className="overview-item">
                                    <span>Fields</span>
                                    <h3>{fields.length}</h3>
                                </div>

                            </div>

                        </div>

                        <div className="sidebar-card">

                            <h3>Actions</h3>

                            <div className="sidebar-actions">

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
                                    onClick={() =>
                                        setPlacementMode(
                                            !placementMode
                                        )
                                    }
                                >
                                    {
                                        placementMode
                                            ? "Finish Placement"
                                            : "Place Fields"
                                    }
                                </button>

                            </div>

                        </div>

                        <div className="sidebar-card">



                            <AddSignerForm
                                documentId={id}
                                onSignerAdded={loadSigners}
                            />

                        </div>

                        <div className="sidebar-card">

                            <h3>Signers</h3>

                            <div className="signer-grid">

                                {
                                    signers.map((signer) => (

                                        <div
                                            key={signer.id}
                                            className="signer-card"
                                        >

                                            <h4>
                                                {signer.name}
                                            </h4>

                                            <p>
                                                {signer.email}
                                            </p>

                                            <span
                                                className={`status-badge status-${signer.status.toLowerCase()}`}
                                            >
                                                {signer.status}
                                            </span>

                                        </div>

                                    ))
                                }

                            </div>

                        </div>

                    </div>

                </div>

            </div>
        </Layout>
    );
}

export default DocumentDetails;