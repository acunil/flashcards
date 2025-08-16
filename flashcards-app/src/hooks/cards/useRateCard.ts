const baseUrl = "http://localhost:8080/api/cards";

const useRateCard = () => {
  const rateCard = (id: string, rating: number) => {
    fetch(`${baseUrl}/${id}/rate?rating=${rating}`, {
      method: "POST",
    }).catch((error) => {
      // Optionally log error or ignore it
      console.error("Failed to send rating", error);
    });
  };

  return { rateCard };
};

export default useRateCard;
