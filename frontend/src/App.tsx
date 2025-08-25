import { BrowserRouter } from "react-router-dom";
import { AppRoutes } from "./routes/routes";
import { AppProvider } from "./contexts";
import { ReviseSettingsProvider } from "./contexts/ReviseSettingsProvider";
import { Auth0Provider, useAuth0 } from "@auth0/auth0-react";
import React from "react";

const AuthGuard = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, isLoading, loginWithRedirect } = useAuth0();

  React.useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      loginWithRedirect(); // redirect to Auth0 if not logged in
    }
  }, [isLoading, isAuthenticated, loginWithRedirect]);

  if (isLoading) {
    return (
      <div className="fixed inset-0 bg-green-200 flex items-center justify-center z-50">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-black border-t-transparent"></div>
      </div>
    );
  }

  return <>{children}</>;
};

function App() {
  return (
    <Auth0Provider
      domain={import.meta.env.VITE_AUTH0_DOMAIN!}
      clientId={import.meta.env.VITE_AUTH0_CLIENT_ID!}
      authorizationParams={{
        redirect_uri: window.location.origin,
        audience: import.meta.env.VITE_AUTH0_AUDIENCE, // if you have an API
        scope: "openid profile email", // adjust based on your needs
      }}
    >
      <AuthGuard>
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
      </AuthGuard>
    </Auth0Provider>
  );
}

export default App;
