import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getDocuments,
  uploadDocument
} from "../services/documentService";

function Documents() {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);

  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState("");

  const loadDocuments = async () => {
    try {
      const data = await getDocuments();
      setDocuments(data);
    } catch (error) {
      console.error("Failed to fetch documents:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDocuments();
  }, []);

  const handleUpload = async (e) => {
    e.preventDefault();

    if (!selectedFile) {
      setUploadError("Please select a PDF file");
      return;
    }

    try {
      setUploading(true);
      setUploadError("");

      await uploadDocument(selectedFile);

      alert("Document uploaded successfully");

      setSelectedFile(null);

      await loadDocuments();

      e.target.reset();

    } catch (error) {

      console.error(error);

      setUploadError(
        error.response?.data?.message ||
        "Upload failed"
      );

    } finally {

      setUploading(false);

    }
  };

  if (loading) {
    return <h2>Loading documents...</h2>;
  }

  return (
    <div>

      <Link to="/dashboard">
        ← Back to Dashboard
      </Link>

      <h1>Documents</h1>

      <hr />

      <h2>Upload Document</h2>

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

      {uploadError && (
        <p style={{ color: "red" }}>
          {uploadError}
        </p>
      )}

      <hr />

      {documents.length === 0 ? (
        <p>No documents found.</p>
      ) : 
            ( documents.map((document) => (
        <Link
            key={document.id}
            to={`/documents/${document.id}`}
            style={{
            textDecoration: "none",
            color: "inherit"
            }}
        >
            <div
            style={{
                border: "1px solid #ccc",
                padding: "10px",
                marginBottom: "10px",
                borderRadius: "5px",
                cursor: "pointer"
            }}
            >
            <p>
                <strong>File:</strong>{" "}
                {document.originalFileName}
            </p>

            <p>
                <strong>Status:</strong>{" "}
                {document.status}
            </p>
            </div>
        </Link>
        ))
      )}

    </div>
  );
}

export default Documents;