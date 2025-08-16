import { BrowserRouter } from "react-router-dom";
import { AppRoutes } from "./routes/routes";
import { DeckProvider } from "./contexts";

function App() {
  return (
    <div className="min-h-screen">
      <div className="mx-auto overflow-visible">
        <BrowserRouter>
          <DeckProvider>
            <AppRoutes />
          </DeckProvider>
        </BrowserRouter>
      </div>
    </div>
  );
}

export default App;
