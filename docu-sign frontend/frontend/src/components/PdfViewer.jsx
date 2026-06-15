import { useEffect, useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";

import "react-pdf/dist/Page/TextLayer.css";
import "react-pdf/dist/Page/AnnotationLayer.css";

import {
  createSignatureField
} from "../services/signatureFieldService";

import SignatureFieldOverlay
  from "./SignatureFieldOverlay";

pdfjs.GlobalWorkerOptions.workerSrc =
  `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

function PdfViewer({
  pdfBlob,
  documentId,
  selectedSignerId,
  fields,
  onFieldCreated,
  documentStatus,
  signers
}) {

  const [pdfUrl, setPdfUrl] =
    useState(null);

  const [numPages, setNumPages] =
    useState(null);

    const [
  creatingField,
  setCreatingField
] = useState(false);

  useEffect(() => {

    if (!pdfBlob) return;

    const url =
      URL.createObjectURL(pdfBlob);

    setPdfUrl(url);

    return () => {
      URL.revokeObjectURL(url);
    };

  }, [pdfBlob]);

  const onLoadSuccess = ({
    numPages
  }) => {

    setNumPages(numPages);

  };

  const handlePageClick = async (
    event,
    pageNumber
  ) => {

        if (documentStatus !== "UPLOADED") {

  alert(
    "Fields can only be added before sending the document"
  );

  return;
}

    

    if (!selectedSignerId) {

      alert(
        "Please select a signer first"
      );

      return;
    }

    if (creatingField) {
  return;
}

    const rect =
      event.currentTarget
        .getBoundingClientRect();

    const clickX =
      event.clientX - rect.left;

    const clickY =
      event.clientY - rect.top;

    const displayedWidth =
      rect.width;

    const displayedHeight =
      rect.height;

    /*
      Temporary values.

      Day 20 improvement:
      Read actual PDF dimensions
      dynamically from PDF.js.
    */

    const pdfWidth = 612;
    const pdfHeight = 792;

    const scaleX =
      pdfWidth / displayedWidth;

    const scaleY =
      pdfHeight / displayedHeight;

   const fieldWidth = 150;
    const fieldHeight = 50;

    const pdfX = Math.max(
  0,
  (clickX * scaleX)
  - (fieldWidth / 2)
);

const pdfY = Math.max(
  0,
  pdfHeight -
  (clickY * scaleY)
  - (fieldHeight / 2)
);

setCreatingField(true);

    try {

  await createSignatureField(
    documentId,
    {
      signerId: selectedSignerId,
      pageNumber,
      xPosition: pdfX,
      yPosition: pdfY,
      width: fieldWidth,
      height: fieldHeight,
      required: true
    }
  );

  await onFieldCreated();

} catch (error) {

  alert(
    error.response?.data?.message ||
    "Failed to create signature field"
  );

} finally {

  setCreatingField(false);

}

  }

  if (!pdfUrl) {
    return <p>Loading PDF...</p>;
  }

  return (
    <Document
      file={pdfUrl}
      onLoadSuccess={onLoadSuccess}
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
    marginBottom: "20px",
    width: "800px"
  }}
>

                <div
  style={{
    position: "relative",
    width: "800px"
  }}
  onClick={(event) =>
    handlePageClick(
      event,
      pageNumber
    )
  }
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
  .map(field => {

    const signer =
      signers.find(
        s =>
          s.id ===
          field.signerId
      );

    return (

      <SignatureFieldOverlay
        key={field.id}
        field={field}
        pageWidth={612}
        pageHeight={792}
        renderedWidth={800}
        signerName={
          signer?.name ||
          "Signer"
        }
      />

    );
  })
  }

</div>

              </div>
            );
          }
        )
      }
    </Document>
  );
}

export default PdfViewer;