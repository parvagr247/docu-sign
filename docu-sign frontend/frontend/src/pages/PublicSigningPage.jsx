import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import {
    getSigningSession, submitSignature
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

    useEffect(() => {

        const loadSession = async () => {

            try {

                const data =
                    await getSigningSession(token);

                setSession(data);

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