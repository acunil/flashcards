import { BrowserRouter } from "react-router-dom";
import { AppRoutes } from "./routes/routes";
import { AppProvider } from "./contexts";

function App() {
  return (
    <div className="min-h-screen">
      <div className="mx-auto overflow-visible">
        <BrowserRouter>
          <AppProvider>
            <AppRoutes />
          </AppProvider>
        </BrowserRouter>
      </div>
    </div>
  );
}

export default App;
