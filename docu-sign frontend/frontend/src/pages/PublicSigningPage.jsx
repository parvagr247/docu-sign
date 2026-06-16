import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import {
    getSigningSession, submitSignature, completeField
} from "../services/publicSigningService";

import SignatureCanvas
    from "../components/SignatureCanvas";


function PublicSigningPage() {

    const { token } = useParams();

    const [session, setSession] =
        useState(null);

    const [loading, setLoading] =
        useState(true);

    const [signatureDataUrl,
        setSignatureDataUrl]
        = useState(null);

    const [submitting, setSubmitting] =
        useState(false);

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

    const handleSubmitSignature =
        async () => {

            if (!signatureDataUrl) {

                alert(
                    "Please save a signature first"
                );

                return;
            }

            try {

                setSubmitting(true);

                const blob =
                    dataUrlToBlob(
                        signatureDataUrl
                    );

                console.log(blob);
                console.log(blob.type);
                console.log(blob.size);

                const response =
                    await submitSignature(
                        token,
                        blob
                    );

                console.log(
                    response
                );

                alert(
                    "Document signed successfully"
                );

            } catch (error) {

                console.error(error);

                alert(
                    "Failed to sign document"
                );

            } finally {

                setSubmitting(false);
            }
        };

    const handleFieldClick = async (
  fieldId
) => {

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


    useEffect(() => {

        const loadSession = async () => {

            try {

                const data =
                    await getSigningSession(token);

                setSession(data);
                setFields(data.fields);

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
        <div>

            <h1>Document Signature</h1>

            <p>
                Signer: {session.signerName}
            </p>

            <p>
                Email: {session.signerEmail}
            </p>

            <p>
                Document: {session.documentName}
            </p>

            <SignatureCanvas
                onSave={setSignatureDataUrl}
            />

            {signatureDataUrl && (
                <div>

                    <h3>
                        Signature Preview
                    </h3>

                    <h3>Fields</h3>

{fields.map(field => (

  <div
    key={field.id}
    style={{
      border: "1px solid black",
      padding: "10px",
      marginBottom: "10px",
      cursor: "pointer"
    }}
    onClick={() =>
      handleFieldClick(field.id)
    }
  >

    <div>
      Page:
      {" "}
      {field.pageNumber}
    </div>

    <div>
      Status:
      {" "}
      {field.completed
        ? "Completed"
        : "Pending"}
    </div>

    {
      field.completed &&
      signatureDataUrl && (

        <img
  src={
    `http://localhost:9099/api/public/sign/${token}/signature`
  }
  alt="signature"
  style={{
    width: 150
  }}
/>

      )
    }

  </div>

))}



                    <img
                        src={signatureDataUrl}
                        alt="signature"
                        style={{
                            width: 250
                        }}
                    />

                </div>
            )}

            <button
                onClick={
                    handleSubmitSignature
                }
                disabled={
                    submitting
                }
            >

                {
                    submitting
                        ? "Signing..."
                        : "Sign Document"
                }

            </button>

        </div>
    );
}

export default PublicSigningPage;