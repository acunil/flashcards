import { BrowserRouter } from "react-router-dom";
import { AppRoutes } from "./routes/routes";
import { AppProvider } from "./contexts";
import { ReviseSettingsProvider } from "./contexts/ReviseSettingsProvider";

function App() {
  return (
    <div className="min-h-screen select-none">
      <div className="mx-auto overflow-visible">
        <BrowserRouter>
          <AppProvider>
            <ReviseSettingsProvider>
              <AppRoutes />
            </ReviseSettingsProvider>
          </AppProvider>
        </BrowserRouter>
      </div>
    </div>
  );
}

export default App;
