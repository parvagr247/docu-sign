import "./styles/SignatureFieldOverlay.css";

function SignatureFieldOverlay({
  field,
  pageWidth,
  pageHeight,
  renderedWidth,
  signerName
}) {

  const scale =
    renderedWidth / pageWidth;

  const style = {

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
      field.height * scale

  };

  return (

    <div
      className="signature-overlay"
      style={style}
    >

      <div className="signature-overlay-name">

        {signerName}

      </div>

      <div className="signature-overlay-text">

        Sign Here

      </div>

    </div>

  );
}

export default SignatureFieldOverlay;