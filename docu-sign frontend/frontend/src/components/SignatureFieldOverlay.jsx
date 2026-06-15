function SignatureFieldOverlay({
  field,
  pageWidth,
  pageHeight,
  renderedWidth,
  signerName
}) {

  const scale =
    renderedWidth / pageWidth;

  return (
    <div
      style={{
        position: "absolute",

        left:
          field.xPosition * scale,

        top:
          (
            pageHeight -
            field.yPosition -
            field.height
          ) * scale,

        width:
          field.width * scale,

        height:
          field.height * scale,

        border:
          "2px dashed red",

        background:
          "rgba(255,0,0,0.1)",

        display: "flex",

        flexDirection: "column",

        alignItems: "center",

        justifyContent: "center",

        fontSize: "10px",

        pointerEvents: "none"
      }}
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

export default SignatureFieldOverlay;