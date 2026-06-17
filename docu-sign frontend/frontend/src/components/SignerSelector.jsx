import "./styles/SignerSelector.css";

function SignerSelector({
  signers,
  selectedSignerId,
  onChange
}) {

  return (

    <div className="signer-selector">

      <label>

        Active Signer

      </label>

      <select
        value={selectedSignerId}
        onChange={(e) =>
          onChange(e.target.value)
        }
      >

        <option value="">

          Choose Signer

        </option>

        {
          signers.map((signer) => (

            <option
              key={signer.id}
              value={signer.id}
            >
              {signer.name}
            </option>

          ))
        }

      </select>

    </div>

  );
}

export default SignerSelector;