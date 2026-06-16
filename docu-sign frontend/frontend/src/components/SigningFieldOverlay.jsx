function SigningFieldOverlay({
  field,
  pageWidth,
  pageHeight,
  renderedWidth,
  signatureImageUrl,
  onClick
}) {

  const scale =
    renderedWidth / pageWidth;

  return (

    <div
      onClick={onClick}
      style={{

        position: "absolute",

        left:
          field.xPosition * scale,

        top:
          (
            pageHeight
            - field.yPosition
            - field.height
          ) * scale,

        width:
          field.width * scale,

        height:
          field.height * scale,

        cursor: "pointer",

        display: "flex",

        alignItems: "center",

        justifyContent: "center",

        overflow: "hidden",

        border:
          field.completed
            ? "none"
            : "2px dashed blue",

        background:
          field.completed
            ? "transparent"
            : "rgba(0,0,255,0.1)"
      }}
    >

      {
        field.completed
          ? (
              <img
                src={signatureImageUrl}
                alt="signature"
                style={{
                  width: "100%",
                  height: "100%",
                  objectFit: "contain"
                }}
              />
            )
          : (
              <span>
                Sign Here
              </span>
            )
      }

    </div>

  );
}

export default SigningFieldOverlay;