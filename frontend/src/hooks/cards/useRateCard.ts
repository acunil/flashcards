import { API_URL } from "../urls";

const useRateCard = () => {
  const rateCard = (id: number, rating: number) => {
    fetch(`${API_URL}/cards/${id}/rate?rating=${rating}`, {
      method: "PATCH",
    }).catch((error) => {
      console.error("Failed to send rating", error);
    });
  };

  return { rateCard };
};

export default useRateCard;
