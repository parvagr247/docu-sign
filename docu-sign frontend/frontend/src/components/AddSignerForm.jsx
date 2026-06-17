import { useState } from "react";
import { createSigner } from "../services/signerService";
import "./styles/AddSignerForm.css";

function AddSignerForm({
  documentId,
  onSignerAdded
}) {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      setLoading(true);
      setError("");

      await createSigner(
        documentId,
        {
          name,
          email
        }
      );

      setName("");
      setEmail("");

      await onSignerAdded();

    } catch (err) {

      console.error(err);

      setError(
        err.response?.data?.message ||
        "Failed to create signer"
      );

    } finally {

      setLoading(false);

    }
  };

  return (

    <form
      className="add-signer-form"
      onSubmit={handleSubmit}
    >

      <div className="form-group">

        <label>
          Signer Name
        </label>

        <input
          type="text"
          placeholder="Enter full name"
          value={name}
          onChange={(e) =>
            setName(e.target.value)
          }
        />

      </div>

      <div className="form-group">

        <label>
          Email Address
        </label>

        <input
          type="email"
          placeholder="Enter email address"
          value={email}
          onChange={(e) =>
            setEmail(e.target.value)
          }
        />

      </div>

      {
        error && (
          <p className="form-error">
            {error}
          </p>
        )
      }

      <button
        className="add-signer-btn"
        type="submit"
        disabled={loading}
      >
        {
          loading
            ? "Adding Signer..."
            : "+ Add Signer"
        }
      </button>

    </form>

  );
}

export default AddSignerForm;