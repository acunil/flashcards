import { API_URL } from "../urls";
import { useAuthFetch } from "../../utils/authFetch";

const useRateCard = () => {
  const { authFetch } = useAuthFetch();

  const rateCard = async (id: number, rating: number) => {
    try {
      const result = await authFetch(
        `${API_URL}/cards/${id}/rate?rating=${rating}`,
        {
          method: "PATCH",
        }
      );

      if (result === undefined) {
        // User was likely redirected to login
        return;
      }
    } catch (error) {
      console.error("Failed to send rating", error);
    }
  };

  return { rateCard };
};

export default useRateCard;
