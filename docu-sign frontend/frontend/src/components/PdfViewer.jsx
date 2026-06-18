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
    setFields,
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

    const [pageDimensionsMap, setPageDimensionsMap] = useState({});

    const handlePageLoadSuccess = (page) => {
        const dims = getPageOriginalDimensions(page);
        setPageDimensionsMap(prev => ({
            ...prev,
            [page.pageNumber]: {
                ...dims,
                rotation: page.rotate || 0
            }
        }));
    };

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

        const dims = pageDimensionsMap[pageNumber] || { width: 612, height: 792, rotation: 0 };
        const rotation = dims.rotation || 0;
        const isRotated = rotation === 90 || rotation === 270;
        const pdfWidth = isRotated ? dims.height : dims.width;
        const pdfHeight = isRotated ? dims.width : dims.height;

        const scaleX =
            pdfWidth / displayedWidth;

        const scaleY =
            pdfHeight / displayedHeight;

        const fieldWidth = 150;
        const fieldHeight = 50;

        const pdfX = Math.max(
            0,
            Math.min(
                pdfWidth - fieldWidth,
                (clickX * scaleX) - (fieldWidth / 2)
            )
        );

        const pdfY = Math.max(
            0,
            Math.min(
                pdfHeight - fieldHeight,
                pdfHeight - (clickY * scaleY) - (fieldHeight / 2)
            )
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

        setFields(prev => [...(prev || []), newField]);
        setCreatingField(true);

        try {

            const savedField = await createSignatureField(
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

            // Replace temp field with backend response.
            // If the field was dragged while it was saving, preserve the coordinates
            // and asynchronously send an update to the backend.
            setFields(prev => {
                const currentTemp = prev.find(f => f.id === tempId);
                const currentX = currentTemp ? currentTemp.xPosition : savedField.xPosition;
                const currentY = currentTemp ? currentTemp.yPosition : savedField.yPosition;

                const finalField = {
                    ...savedField,
                    xPosition: currentX,
                    yPosition: currentY
                };

                if (currentX !== savedField.xPosition || currentY !== savedField.yPosition) {
                    updateSignatureField(savedField.id, {
                        xPosition: currentX,
                        yPosition: currentY
                    }).catch(console.error);
                }

                return [...prev.filter(f => f.id !== tempId), finalField];
            });

        } catch (error) {

            // Revert optimistic insert on error
            setFields(prev => prev.filter(f => f.id !== tempId));
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

        const field = fields.find(
            f => f.id === active.id
        );

        if (!field) return;

        const dims = pageDimensionsMap[field.pageNumber] || { width: 612, height: 792, rotation: 0 };
        const rotation = dims.rotation || 0;
        const isRotated = rotation === 90 || rotation === 270;
        const pageWidth = isRotated ? dims.height : dims.width;
        const pageHeight = isRotated ? dims.width : dims.height;
        const renderedWidth = 800;

        const scale =
            pageWidth / renderedWidth;

        const newX = Math.max(
            0,
            Math.min(
                pageWidth - field.width,
                field.xPosition + delta.x * scale
            )
        );

        const newY = Math.max(
            0,
            Math.min(
                pageHeight - field.height,
                field.yPosition - delta.y * scale
            )
        );

        // Keep track of old state for rollback
        const originalFields = [...fields];

        // Optimistically update coordinates in parent state
        setFields(prev =>
            prev.map(f =>
                f.id === field.id
                    ? { ...f, xPosition: newX, yPosition: newY }
                    : f
            )
        );

        // Only save to backend if it is a real field (not temp)
        if (!field.id.toString().startsWith("temp-")) {
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

                // Update parent state with response from backend
                setFields(prev =>
                    prev.map(f => f.id === field.id ? response : f)
                );

            } catch (error) {

                console.error(error);
                // Revert back on error
                setFields(originalFields);

            }
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
                                            fields
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

                                                    const dims = pageDimensionsMap[pageNumber] || { width: 612, height: 792, rotation: 0 };
                                                    const isRotated = dims.rotation === 90 || dims.rotation === 270;
                                                    const visualPageWidth = isRotated ? dims.height : dims.width;
                                                    const visualPageHeight = isRotated ? dims.width : dims.height;

                                                    return (

                                                        <DraggableSignatureField
                                                            key={field.id}
                                                            field={field}
                                                            pageWidth={visualPageWidth}
                                                            pageHeight={visualPageHeight}
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