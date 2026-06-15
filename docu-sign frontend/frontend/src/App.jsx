import { BrowserRouter, Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import Documents from "./pages/Document";
import DocumentDetails from "./pages/DocumentDetails";

function App() {

  return (
    <BrowserRouter>
      <Routes>

        <Route
          path="/"
          element={<Login />}
        />

        <Route path="/documents" 
        element={ <ProtectedRoute> <Documents /> </ProtectedRoute> }
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />

        <Route path="/documents/:id"
          element={
            <ProtectedRoute>
              <DocumentDetails />
            </ProtectedRoute>
          }
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;