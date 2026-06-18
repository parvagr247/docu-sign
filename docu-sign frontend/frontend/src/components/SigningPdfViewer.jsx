import { Document, Page, pdfjs } from "react-pdf";

import "react-pdf/dist/Page/TextLayer.css";
import "react-pdf/dist/Page/AnnotationLayer.css";

import { useEffect, useState } from "react";

import SigningFieldOverlay
  from "./SigningFieldOverlay";

pdfjs.GlobalWorkerOptions.workerSrc =
  `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

const getPageOriginalDimensions = (page) => {
  if (page.originalWidth && page.originalHeight) {
    return { width: page.originalWidth, height: page.originalHeight };
  }
  if (page.view) {
    return {
      width: page.view[2] - page.view[0],
      height: page.view[3] - page.view[1]
    };
  }
  return { width: page.width, height: page.height };
};

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

  const [pageDimensionsMap, setPageDimensionsMap] = useState({});

  const handlePageLoadSuccess = (page) => {
    const dims = getPageOriginalDimensions(page);
    setPageDimensionsMap(prev => ({
      ...prev,
      [page.pageNumber]: dims
    }));
  };

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
                  marginBottom: "20px",
                  overflow: "visible"
                }}
              >

                <Page
                  pageNumber={pageNumber}
                  width={800}
                  onLoadSuccess={handlePageLoadSuccess}
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

                        pageWidth={pageDimensionsMap[pageNumber]?.width || 612}

                        pageHeight={pageDimensionsMap[pageNumber]?.height || 792}

                        renderedWidth={800}

                        signatureImageUrl={
                          signatureImageUrl
                        }

                        onClick={() => {

                          console.log(
                            "FIELD ID",
                            field.id
                          );

                          onFieldClick(
                            field.id
                          );

                        }}
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