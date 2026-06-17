import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "./styles/PublicSigningPage.css";

import {
    getSigningSession, saveSignature, completeField, downloadSigningPdf, completeSigning
} from "../services/publicSigningService";

import SigningPdfViewer
    from "../components/SigningPdfViewer";


import SignatureCanvas
    from "../components/SignatureCanvas";


function PublicSigningPage() {

    const { token } = useParams();

    const [session, setSession] =
        useState(null);

    const [loading, setLoading] =
        useState(true);

    const [pdfBlob, setPdfBlob] =
        useState(null);

    const [signatureDataUrl,
        setSignatureDataUrl]
        = useState(null);

    const [submitting, setSubmitting] =
    useState(false);

const [completed, setCompleted] =
    useState(false);

const [message, setMessage] =
    useState("");

    const dataUrlToBlob = (
        dataUrl
    ) => {

        const arr =
            dataUrl.split(",");

        const mime =
            arr[0].match(/:(.*?);/)[1];

        const binary =
            atob(arr[1]);

        let length =
            binary.length;

        const uint8Array =
            new Uint8Array(length);

        while (length--) {

            uint8Array[length] =
                binary.charCodeAt(length);

        }

        return new Blob(
            [uint8Array],
            { type: mime }
        );
    };

    const [fields, setFields] = useState([]);


    const handleSignatureSave =
        async (dataUrl) => {

            try {

                const blob =
                    dataUrlToBlob(dataUrl);

                await saveSignature(
                    token,
                    blob
                );

                setSignatureDataUrl(
                    dataUrl
                );

                const updatedSession =
                    await getSigningSession(
                        token
                    );

                setSession(
                    updatedSession
                );

                alert(
                    "Signature saved"
                );

            } catch (error) {

                console.error(error);

                alert(
                    "Failed to save signature"
                );
            }
        };

    const handleFieldClick =
        async (fieldId) => {

            console.log(
                "signatureDataUrl",
                signatureDataUrl
            );

            console.log(
                "session.signatureImagePath",
                session.signatureImagePath
            );

            if (!signatureDataUrl) {

                alert(
                    "Please create a signature first"
                );

                return;
            }

            try {

                await completeField(
                    token,
                    fieldId
                );

                setFields(
                    current =>
                        current.map(field =>
                            field.id === fieldId
                                ? {
                                    ...field,
                                    completed: true
                                }
                                : field
                        )
                );

            } catch (error) {

                console.error(error);
            }
        };


    const handleFinishSigning =
        async () => {

            try {

                const response =
                    await completeSigning(
                        token
                    );

                setCompleted(true);

setMessage(
    response.message
);

                const updatedSession =
                    await getSigningSession(
                        token
                    );

                setSession(
                    updatedSession
                );

            } catch (error) {

                console.error(error);

                alert(
                    error.response?.data?.message
                    ||
                    "Unable to finish signing"
                );
            }
        };

    useEffect(() => {

        const loadSession = async () => {

            try {

                const data =
                    await getSigningSession(token);

                setSession(data);
                setFields(data.fields);

                const pdf =
                    await downloadSigningPdf(token);

                setPdfBlob(pdf);

            } catch (error) {


                console.log(
                    "FULL ERROR",
                    error.response
                );

                console.log(
                    "RESPONSE DATA",
                    error.response?.data
                );

                console.log(
                    "STATUS",
                    error.response?.status
                );

                console.error(error);

            } finally {

                setLoading(false);
            }
        };

        loadSession();

    }, [token]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!session) {
        return <div>Signing session not found</div>;
    }

    return (
  <div className="signing-page">

    <div className="signing-header">

      <h1>
        Sign Document
      </h1>

      <p>
        Review and complete the document below
      </p>

    </div>

    <div className="signing-info">

      <div>
        <span>Signer</span>
        <h3>{session.signerName}</h3>
      </div>

      <div>
        <span>Email</span>
        <h3>{session.signerEmail}</h3>
      </div>

      <div>
        <span>Document</span>
        <h3>{session.documentName}</h3>
      </div>

    </div>

    <div className="progress-card">

      <h3>
        Progress
      </h3>

      <div className="progress-bar">

        <div
          className="progress-fill"
          style={{
            width:
              `${(
                fields.filter(
                  field =>
                    field.completed
                ).length
                /
                fields.length
              ) * 100}%`
          }}
        />

      </div>

      <p>

        {
          fields.filter(
            field =>
              field.completed
          ).length
        }

        /

        {fields.length}

        {" "}
        fields completed

      </p>

    </div>

    <div className="signature-section">

      <h2>
        Create Signature
      </h2>

      <SignatureCanvas
        onSave={
          handleSignatureSave
        }
      />

    </div>

    {
      signatureDataUrl && (

        <div className="preview-card">

          <h2>
            Signature Preview
          </h2>

          <img
            src={signatureDataUrl}
            alt="signature"
          />

        </div>

      )
    }

    {
      pdfBlob && (

        <div className="pdf-section">

          <h2>
            Document Preview
          </h2>

          <SigningPdfViewer
            pdfBlob={pdfBlob}
            fields={fields}
            signatureImageUrl={
              `http://localhost:9099/api/public/sign/${token}/signature`
            }
            onFieldClick={
              handleFieldClick
            }
          />

        </div>

      )
    }

    {
      !completed && (

        <button
          className="finish-btn"
          onClick={
            handleFinishSigning
          }
          disabled={submitting}
        >
          Finish Signing
        </button>

      )
    }

    {
      completed && (

        <div className="success-card">

          <h2>
            🎉 Document Signed
          </h2>

          <p>
            {message}
          </p>

          <p>
            You may now close this page.
          </p>

        </div>

      )
    }

  </div>
);
}

export default PublicSigningPage;