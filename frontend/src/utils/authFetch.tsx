// useAuthFetch.ts
import { useAuth0 } from "@auth0/auth0-react";

export const useAuthFetch = () => {
  const { getAccessTokenSilently, loginWithRedirect } = useAuth0();

  const authFetch = async (url: string, options: RequestInit = {}) => {
    let token: string;
    try {
      token = await getAccessTokenSilently({
        authorizationParams: { audience: import.meta.env.VITE_AUTH0_AUDIENCE },
      });
    } catch (err: unknown) {
      if (
        typeof err === "object" &&
        err !== null &&
        "error" in err &&
        ((err as { error: string }).error === "consent_required" ||
          (err as { error: string }).error === "login_required")
      ) {
        loginWithRedirect({
          authorizationParams: {
            audience: import.meta.env.VITE_AUTH0_AUDIENCE,
          },
        });
        return;
      } else {
        throw err;
      }
    }

    const response = await fetch(url, {
      ...options,
      headers: {
        ...options.headers,
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok)
      throw new Error(`Request failed with status ${response.status}`);

    if (response.status === 204) {
      return;
    }
    return response.json();
  };

  return { authFetch };
};
