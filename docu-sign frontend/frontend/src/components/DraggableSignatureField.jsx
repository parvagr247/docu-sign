import {
  useDraggable
} from "@dnd-kit/core";

function DraggableSignatureField({
  field,
  pageWidth,
  pageHeight,
  renderedWidth,
  signerName
}) {

  console.log("RENDER FIELD", field.id);

  const {
  attributes,
  listeners,
  setNodeRef,
  transform
} = useDraggable({
  id: field.id
});

console.log(
  "transform",
  transform
);

console.log(
  "listeners",
  listeners
);

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

  border: "2px dashed red",

  background: "rgba(255,0,0,0.1)",

  display: "flex",

  flexDirection: "column",

  alignItems: "center",

  justifyContent: "center",

  cursor: "move",

  transform:
    transform
      ? `translate3d(${transform.x}px, ${transform.y}px, 0)`
      : undefined
};

  return (
    <div
  ref={setNodeRef}
  style={style}
  {...listeners}
  {...attributes}
>
      <strong>
        {signerName}
      </strong>

      <span>
        SIGN HERE
      </span>
    </div>
  );
}

export default DraggableSignatureField;