import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login } from "../services/authService";
import { saveAuthData } from "../utils/token";

function Login() {

  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {

    try {

      const response = await login(email, password);

      saveAuthData(response);

      navigate("/dashboard");

    } catch (error) {

      console.error(error);

        alert(error.message);

    }

  };

  return (
    <div>

      <h1>Login</h1>

      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
      />

      <br />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <br />

      <button onClick={handleLogin}>
        Login
      </button>

    </div>
  );
}

export default Login;