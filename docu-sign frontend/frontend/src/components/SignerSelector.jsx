function SignerSelector({
  signers,
  selectedSignerId,
  onChange
}) {

  return (
    <div>

      <h3>Select Signer</h3>

      <select
        value={selectedSignerId}
        onChange={(e) =>
          onChange(e.target.value)
        }
      >
        <option value="">
          Select Signer
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