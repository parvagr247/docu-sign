import { useState } from "react";
import { createSigner } from "../services/signerService";

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
    <div>

      <h2>Add Signer</h2>

      <form onSubmit={handleSubmit}>

        <input
          type="text"
          placeholder="Signer Name"
          value={name}
          onChange={(e) =>
            setName(e.target.value)
          }
        />

        <br /><br />

        <input
          type="email"
          placeholder="Signer Email"
          value={email}
          onChange={(e) =>
            setEmail(e.target.value)
          }
        />

        <br /><br />

        <button
          type="submit"
          disabled={loading}
        >
          {loading
            ? "Adding..."
            : "Add Signer"}
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

export default AddSignerForm;