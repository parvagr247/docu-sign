import { useEffect, useRef } from "react";
import SignaturePad from "signature_pad";

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
    <div>

      <canvas
        ref={canvasRef}
        width={500}
        height={200}
        style={{
          border:
            "1px solid #ccc"
        }}
      />

      <div>

        <button
          onClick={handleClear}
        >
          Clear
        </button>

        <button
          onClick={handleSave}
        >
          Save Signature
        </button>

      </div>

    </div>
  );
}

export default SignatureCanvas;