import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getDocuments,
  uploadDocument
} from "../services/documentService";

import "./styles/Documents.css";
import Layout from "../components/Layout";

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
  <Layout>
  <div className="documents-page">

    <div className="documents-header">

      <div className="documents-stats">

  <div className="stat-card">
    <h3>{documents.length}</h3>
    <p>Total Documents</p>
  </div>

  <div className="stat-card">
    <h3>
      {
        documents.filter(
          d => d.status === "SIGNED"
        ).length
      }
    </h3>
    <p>Completed</p>
  </div>

  <div className="stat-card">
    <h3>
      {
        documents.filter(
          d => d.status === "PARTIALLY_SIGNED"
        ).length
      }
    </h3>
    <p>In Progress</p>
  </div>

  <div className="stat-card">
    <h3>
      {
        documents.filter(
          d => d.status === "UPLOADED"
        ).length
      }
    </h3>
    <p>Drafts</p>
  </div>

</div>

      <div>

        <h1>Documents</h1>

        <p>
          Manage and track all signature workflows
        </p>
      </div>

    </div>

    <div className="upload-card">

      <div className="upload-header">

  <h2>📄 Upload New Document</h2>

  <p>
    Upload a PDF and start collecting signatures.
  </p>

</div>

      <form
        onSubmit={handleUpload}
        className="upload-form"
      >

        <input
          type="file"
          accept="application/pdf"
          onChange={(e) =>
            setSelectedFile(
              e.target.files[0]
            )
          }
        />

        <button
          type="submit"
          disabled={uploading}
        >
          {
            uploading
              ? "Uploading..."
              : "Upload PDF"
          }
        </button>

      </form>

      {
        uploadError && (
          <p className="error-text">
            {uploadError}
          </p>
        )
      }

    </div>

    <div className="documents-grid">

      {
        documents.length === 0
          ? (
            <div className="empty-state">

              <h3>No Documents Yet</h3>

              <p>
                Upload your first PDF to begin.
              </p>

            </div>
          )
          : (
            documents.map((document) => (

              <Link
  key={document.id}
  to={`/documents/${document.id}`}
  className="document-card"
>

  <div className="document-icon">
    📄
  </div>

  <h3>
    {document.originalFileName}
  </h3>

  <div className="document-meta">

    <span
      className={`status-badge status-${document.status.toLowerCase()}`}
    >
      {document.status}
    </span>

  </div>

  <div className="document-footer">

    Open →

  </div>

</Link>

            ))
          )
      }

    </div>

  </div>
  </Layout>
);
}

export default Documents;