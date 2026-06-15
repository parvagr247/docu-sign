import { useEffect, useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";

import "react-pdf/dist/Page/TextLayer.css";
import "react-pdf/dist/Page/AnnotationLayer.css";

pdfjs.GlobalWorkerOptions.workerSrc =
  `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

function PdfViewer({ pdfBlob }) {

  const [pdfUrl, setPdfUrl] =
    useState(null);

  const [numPages, setNumPages] =
    useState(null);

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
          (_, index) => (
            <div
              key={index}
              style={{
                marginBottom: "20px"
              }}
            >
              <Page
                pageNumber={index + 1}
                width={800}
              />
            </div>
          )
        )
      }
    </Document>
  );
}

export default PdfViewer;