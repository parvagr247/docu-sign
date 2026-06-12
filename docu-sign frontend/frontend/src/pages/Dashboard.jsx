import { clearAuthData } from "../utils/token";
import { useNavigate } from "react-router-dom";

function Dashboard() {

  const navigate = useNavigate();

  const logout = () => {

    clearAuthData();

    navigate("/");

  };

  return (
    <div>

      <h1>Dashboard</h1>

      <button onClick={logout}>
        Logout
      </button>

    </div>
  );
}

export default Dashboard;