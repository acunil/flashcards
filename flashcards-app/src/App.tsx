import { BrowserRouter } from "react-router-dom";
import { AppRoutes } from "./routes/routes";

function App() {
  return (
    <div className="min-h-screen">
      {" "}
      {/* full width background */}
      <div className="mx-auto overflow-visible">
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </div>
    </div>
  );
}

export default App;
