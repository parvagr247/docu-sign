import { useEffect, useState } from "react";
import { Document, Page, pdfjs } from "react-pdf";

import "react-pdf/dist/Page/TextLayer.css";
import "react-pdf/dist/Page/AnnotationLayer.css";

import "./styles/PdfViewer.css";

import {
    DndContext,
    PointerSensor,
    useSensor,
    useSensors
} from "@dnd-kit/core";

import DraggableSignatureField
    from "./DraggableSignatureField";

import {
    createSignatureField,
    updateSignatureField
} from "../services/signatureFieldService";

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

function PdfViewer({
    pdfBlob,
    documentId,
    selectedSignerId,
    fields,
    onFieldCreated,
    documentStatus,
    signers,
    placementMode
}) {

    const sensors = useSensors(
        useSensor(
            PointerSensor,
            {
                activationConstraint: {
                    distance: 5
                }
            }
        )
    );


    const [pdfUrl, setPdfUrl] =
        useState(null);

    const [numPages, setNumPages] =
        useState(null);

    const [
        creatingField,
        setCreatingField
    ] = useState(false);

    const [isDragging, setIsDragging] =
        useState(false);

    const [lastDragTime, setLastDragTime] =
        useState(0);

    const [localFields, setLocalFields] = useState(fields);

    const [pageDimensionsMap, setPageDimensionsMap] = useState({});

    const handlePageLoadSuccess = (page) => {
        const dims = getPageOriginalDimensions(page);
        setPageDimensionsMap(prev => ({
            ...prev,
            [page.pageNumber]: dims
        }));
    };

    useEffect(() => {
        setLocalFields(fields);
    }, [fields]);

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

        console.log("placementMode =", placementMode);

        if (!placementMode) {
            return;
        }



        console.log(
            "CREATE FIELD TRIGGERED"
        );

        const now = Date.now();

        if (
            now - lastDragTime < 500
        ) {
            return;
        }

        if (isDragging) {
            return;
        }

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

        const dims = pageDimensionsMap[pageNumber] || { width: 612, height: 792 };
        const pdfWidth = dims.width;
        const pdfHeight = dims.height;

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

        const tempId = `temp-${Date.now()}`;
        const newField = {
            id: tempId,
            signerId: selectedSignerId,
            pageNumber,
            xPosition: pdfX,
            yPosition: pdfY,
            width: fieldWidth,
            height: fieldHeight,
            required: true
        };

        setLocalFields(prev => [...(prev || []), newField]);
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

            // Revert optimistic insert on error
            setLocalFields(prev => prev.filter(f => f.id !== tempId));
            alert(
                error.response?.data?.message ||
                "Failed to create signature field"
            );

        } finally {

            setCreatingField(false);

        }

    };

    const handleDragEnd = async (event) => {

        console.log("DRAG END EVENT", event);

        const { active, delta } = event;

        console.log("ACTIVE", active.id);
        console.log("DELTA", delta);

        const field = localFields.find(
            f => f.id === active.id
        );

        console.log(
            "FIELD BEFORE",
            JSON.stringify(field, null, 2)
        );

        if (!field) return;

        console.log(
            "OLD X:",
            field.xPosition
        );

        console.log(
            "OLD Y:",
            field.yPosition
        );




        const dims = pageDimensionsMap[field.pageNumber] || { width: 612, height: 792 };
        const pageWidth = dims.width;
        const pageHeight = dims.height;
        const renderedWidth = 800;

        const scale =
            pageWidth / renderedWidth;

        const newX =
            field.xPosition +
            delta.x * scale;

        const newY =
            field.yPosition -
            delta.y * scale;

        console.log(
            "NEW X:",
            newX
        );

        console.log(
            "NEW Y:",
            newY
        );

        console.log(
            "NEW POSITION",
            {
                oldX: field.xPosition,
                oldY: field.yPosition,
                newX,
                newY
            }
        );

        // Optimistically update coordinates in local state
        setLocalFields(prev =>
            prev.map(f =>
                f.id === field.id
                    ? { ...f, xPosition: newX, yPosition: newY }
                    : f
            )
        );

        try {

            const response =
                await updateSignatureField(
                    field.id,
                    {
                        xPosition: newX,
                        yPosition: newY
                    }
                );

            console.log(
                "UPDATE RESPONSE",
                JSON.stringify(response, null, 2)
            );

            await onFieldCreated();

            console.log(
                "UPDATE SUCCESS"
            );

        } catch (error) {

            console.error(error);
            // Revert back on error
            setLocalFields(fields);

        }
    };



    if (!pdfUrl) {

  return (
    <div className="pdf-viewer-loading">
      Loading PDF...
    </div>
  );
}

    return (

        <DndContext
            sensors={sensors}
            onDragStart={(event) => {

                console.log(
                    "DRAG START",
                    event
                );

                setIsDragging(true);

            }}
            onDragEnd={async (event) => {

                await handleDragEnd(event);

                console.log(
                    "DRAG END"
                );

                setLastDragTime(
                    Date.now()
                );

                setTimeout(() => {
                    setIsDragging(false);
                }, 300);

            }}
        >
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
    className="pdf-page-wrapper"
>


                                    <div className="pdf-page-container">

                                        <div
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
                                                onLoadSuccess={handlePageLoadSuccess}
                                            />
                                        </div>

                                        {
                                            localFields
                                                ?.filter(
                                                    field =>
                                                        field.pageNumber ===
                                                        pageNumber
                                                )
                                                .map(field => {

                                                    const signer =
                                                        signers?.find(
                                                            s =>
                                                                s.id ===
                                                                field.signerId
                                                        );

                                                    return (

                                                        <DraggableSignatureField
                                                            key={field.id}
                                                            field={field}
                                                            pageWidth={pageDimensionsMap[pageNumber]?.width || 612}
                                                            pageHeight={pageDimensionsMap[pageNumber]?.height || 792}
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

        </DndContext>
    );
}

export default PdfViewer;