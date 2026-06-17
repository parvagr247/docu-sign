import { NavLink, useNavigate } from "react-router-dom";
import { clearAuthData } from "../utils/token";
import "./styles/Navbar.css";

function Navbar() {

  const navigate = useNavigate();

  const logout = () => {

    clearAuthData();

    navigate("/");

  };

  return (

    <nav className="navbar">

      <div className="navbar-logo">

        Doc E-Sign

      </div>

      <div className="navbar-links">

        <NavLink
          to="/dashboard"
          className={({ isActive }) =>
            isActive ? "nav-link active" : "nav-link"
          }
        >
          Dashboard
        </NavLink>

        <NavLink
          to="/documents"
          className={({ isActive }) =>
            isActive ? "nav-link active" : "nav-link"
          }
        >
          Documents
        </NavLink>

      </div>

      <button
        className="logout-button"
        onClick={logout}
      >
        Logout
      </button>

    </nav>

  );
}

export default Navbar;