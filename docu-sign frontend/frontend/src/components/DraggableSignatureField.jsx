import {
  useDraggable
} from "@dnd-kit/core";

import "./styles/DraggableSignatureField.css";

function DraggableSignatureField({
  field,
  pageWidth,
  pageHeight,
  renderedWidth,
  signerName
}) {

  const {
    attributes,
    listeners,
    setNodeRef,
    transform
  } = useDraggable({
    id: field.id
  });

  const scale =
    renderedWidth / pageWidth;

  const style = {
    position: "absolute",

    zIndex: 1000,

    touchAction: "none",

    left: field.xPosition * scale,

    top:
      (
        pageHeight
        - field.yPosition
        - field.height
      ) * scale,

    width: field.width * scale,

    height: field.height * scale,

    transform:
      transform
        ? `translate3d(${transform.x}px, ${transform.y}px, 0)`
        : undefined
  };

  return (

    <div
      ref={setNodeRef}
      style={style}
      className="signature-field"
      {...listeners}
      {...attributes}
    >

      <div className="signature-field-name">
        {signerName}
      </div>

      <div className="signature-field-text">
        Sign Here
      </div>

    </div>

  );
}

export default DraggableSignatureField;