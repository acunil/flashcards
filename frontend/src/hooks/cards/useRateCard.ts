const baseUrl = "http://localhost:8080/cards";

const useRateCard = () => {
  const rateCard = (id: number, rating: number) => {
    fetch(`${baseUrl}/${id}/rate?rating=${rating}`, {
      method: "PUT",
    }).catch((error) => {
      console.error("Failed to send rating", error);
    });
  };

  return { rateCard };
};

export default useRateCard;
