import { useState } from "react";
import { uploadDocument } from "../services/documentService";

function UploadDocumentForm({ onUploadSuccess }) {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");

  const handleUpload = async (e) => {
    e.preventDefault();

    if (!selectedFile) {
      setError("Please select a PDF file");
      return;
    }

    try {
      setUploading(true);
      setError("");

      await uploadDocument(selectedFile);

      setSelectedFile(null);

      alert("Document uploaded successfully");

      onUploadSuccess();

      e.target.reset();
      
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
        "Failed to upload document"
      );
    } finally {
      setUploading(false);
    }
  };

  return (
    <div style={{ marginBottom: "20px" }}>
      <h3>Upload Document</h3>

      <form onSubmit={handleUpload}>
        <input
          type="file"
          accept="application/pdf"
          onChange={(e) =>
            setSelectedFile(e.target.files[0])
          }
        />

        <button
          type="submit"
          disabled={uploading}
          style={{ marginLeft: "10px" }}
        >
          {uploading ? "Uploading..." : "Upload"}
        </button>
      </form>

      {error && (
        <p style={{ color: "red" }}>
          {error}
        </p>
      )}
    </div>
  );
}

export default UploadDocumentForm;