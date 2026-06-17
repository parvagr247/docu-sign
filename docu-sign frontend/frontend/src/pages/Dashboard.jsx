import "./styles/Dashboard.css";
import Layout from "../components/Layout";

function Dashboard() {

  return (
    <Layout>
    <div className="dashboard">

      <div className="dashboard-header">

  <div>

    <h1>
      Welcome Back 👋
    </h1>

    <p>
      Manage documents, signers and workflows
    </p>

  </div>

</div>

      <div className="stats-grid">

        <div className="stat-card">
          <p>Total Documents</p>
          <h2>--</h2>
        </div>

        <div className="stat-card">
          <p>Pending Signatures</p>
          <h2>--</h2>
        </div>

        <div className="stat-card">
          <p>Completed Documents</p>
          <h2>--</h2>
        </div>

        <div className="stat-card">
          <p>Sent Today</p>
          <h2>--</h2>
        </div>

      </div>

    </div>
    </Layout>
  );
}

export default Dashboard;