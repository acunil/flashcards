import { API_URL } from "../urls";

const useRateCard = () => {
  const rateCard = (id: number, rating: number) => {
    fetch(`${API_URL}/cards/${id}/rate?rating=${rating}`, {
      method: "PUT",
    }).catch((error) => {
      console.error("Failed to send rating", error);
    });
  };

  return { rateCard };
};

export default useRateCard;
