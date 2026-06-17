import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login } from "../services/authService";
import { saveAuthData } from "../utils/token";

import "./styles/Login.css";

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
  <div className="login-page">

    <div className="login-card">

      <h1>E-Sign Platform</h1>

      <p>
        Secure document signing made simple
      </p>

      <input
        type="email"
        placeholder="Email Address"
        value={email}
        onChange={(e) =>
          setEmail(e.target.value)
        }
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) =>
          setPassword(e.target.value)
        }
      />

      <button
        onClick={handleLogin}
      >
        Login
      </button>

    </div>

  </div>
);
}

export default Login;