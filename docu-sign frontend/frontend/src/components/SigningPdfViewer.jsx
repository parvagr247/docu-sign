import { Document, Page, pdfjs } from "react-pdf";

import "react-pdf/dist/Page/TextLayer.css";
import "react-pdf/dist/Page/AnnotationLayer.css";

import { useEffect, useState } from "react";

import SigningFieldOverlay
  from "./SigningFieldOverlay";

pdfjs.GlobalWorkerOptions.workerSrc =
  `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

function SigningPdfViewer({

  pdfBlob,

  fields,

  signatureImageUrl,

  onFieldClick

}) {

  const [pdfUrl, setPdfUrl] =
    useState(null);

  const [numPages, setNumPages] =
    useState(null);

  useEffect(() => {

    if (!pdfBlob) {
      return;
    }

    const url =
      URL.createObjectURL(pdfBlob);

    setPdfUrl(url);

    return () => {
      URL.revokeObjectURL(url);
    };

  }, [pdfBlob]);

  const handleLoadSuccess = ({
    numPages
  }) => {

    setNumPages(numPages);

  };

  if (!pdfUrl) {
    return <p>Loading PDF...</p>;
  }

  return (

    <Document
      file={pdfUrl}
      onLoadSuccess={handleLoadSuccess}
    >

      {
        Array.from(
          new Array(numPages),
          (_, index) => {

            const pageNumber =
              index + 1;

            return (

              <div
                key={pageNumber}
                style={{
                  position: "relative",
                  width: "800px",
                  marginBottom: "20px"
                }}
              >

                <Page
                  pageNumber={pageNumber}
                  width={800}
                />

                {
                  fields
                    ?.filter(
                      field =>
                        field.pageNumber ===
                        pageNumber
                    )
                    .map(field => (

                      <SigningFieldOverlay
                        key={field.id}

                        field={field}

                        pageWidth={612}

                        pageHeight={792}

                        renderedWidth={800}

                        signatureImageUrl={
                          signatureImageUrl
                        }

                        onClick={() =>
                          onFieldClick(
                            field.id
                          )
                        }
                      />

                    ))
                }

              </div>

            );
          }
        )
      }

    </Document>

  );
}

export default SigningPdfViewer;