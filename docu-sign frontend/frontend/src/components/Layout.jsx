import Navbar from "./Navbar";

function Layout({ children }) {

  return (

    <>
      <Navbar />

      <main
        style={{
          paddingTop: "30px",
          minHeight: "70vh",
          background: "#f8fafc"
        }}
      >
        {children}
      </main>
    </>

  );
}

export default Layout;