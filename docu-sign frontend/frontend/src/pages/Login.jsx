import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login, register } from "../services/authService";
import { saveAuthData } from "../utils/token";

import "./styles/Login.css";

function Login() {

  const navigate = useNavigate();

  const [isRegistering, setIsRegistering] = useState(false);
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {

    try {

      const response = await login(email, password);

      saveAuthData(response);

      navigate("/dashboard");

    } catch (error) {

      console.error(error);

      alert(error.response?.data?.message || error.message);

    }

  };

  const handleRegister = async () => {

    try {

      await register(fullName, email, password);

      alert("User registered successfully. Please login.");
      setIsRegistering(false);
      setFullName("");
      setPassword("");

    } catch (error) {

      console.error(error);

      alert(error.response?.data?.message || error.message);

    }

  };

  return (
  <div className="login-page">

    <div className="login-card">

      <h1>E-Sign Platform</h1>

      <p>
        {isRegistering ? "Create your account" : "Secure document signing made simple"}
      </p>

      {isRegistering && (
        <input
          type="text"
          placeholder="Full Name"
          value={fullName}
          onChange={(e) =>
            setFullName(e.target.value)
          }
        />
      )}

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

      {isRegistering ? (
        <button
          onClick={handleRegister}
        >
          Register
        </button>
      ) : (
        <button
          onClick={handleLogin}
        >
          Login
        </button>
      )}

      <div className="login-toggle">
        {isRegistering ? (
          <>
            Already have an account?{" "}
            <span onClick={() => setIsRegistering(false)}>Login</span>
          </>
        ) : (
          <>
            Don't have an account?{" "}
            <span onClick={() => setIsRegistering(true)}>Register</span>
          </>
        )}
      </div>

    </div>

  </div>
);
}

export default Login;