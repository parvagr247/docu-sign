import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

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

                alert(
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
                onSave={handleSignatureSave}
            />

            <div
                style={{
                    marginTop: "20px",
                    marginBottom: "20px"
                }}
            >

                <strong>

                    {
                        fields.filter(
                            field => field.completed
                        ).length
                    }

                    /

                    {fields.length}

                    {" "}
                    completed

                </strong>

            </div>



            {signatureDataUrl && (
                <div>

                    <h3>
                        Signature Preview
                    </h3>

                    {
                        pdfBlob && (

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

                        )
                    }



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
                    handleFinishSigning
                }
            >

                Finish Signing

            </button>


        </div>
    );
}

export default PublicSigningPage;