import { useEffect, useRef } from "react";
import SignaturePad from "signature_pad";

import "./styles/SignatureCanvas.css";

function SignatureCanvas({
  onSave
}) {

  const canvasRef = useRef(null);

  const signaturePadRef =
    useRef(null);

  useEffect(() => {

    const canvas =
      canvasRef.current;

    signaturePadRef.current =
      new SignaturePad(canvas);

  }, []);

  const handleClear = () => {

    signaturePadRef.current.clear();

  };

  const handleSave = () => {

    if (
      signaturePadRef.current.isEmpty()
    ) {

      alert(
        "Please draw a signature"
      );

      return;
    }

    const dataUrl =
      signaturePadRef.current.toDataURL(
        "image/png"
      );

    onSave(dataUrl);
  };

  return (

    <div className="signature-container">

      <div className="signature-header">

        <h3>
          Draw Your Signature
        </h3>

        <p>
          Use your mouse or touchpad
        </p>

      </div>

      <canvas
        ref={canvasRef}
        width={600}
        height={220}
        className="signature-canvas"
      />

      <div className="signature-actions">

        <button
          className="clear-btn"
          onClick={handleClear}
        >
          Clear
        </button>

        <button
          className="save-btn"
          onClick={handleSave}
        >
          Save Signature
        </button>

      </div>

    </div>

  );
}

export default SignatureCanvas;