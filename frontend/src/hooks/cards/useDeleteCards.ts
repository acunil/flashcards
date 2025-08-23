import { API_URL } from "../urls";
import { useAppContext } from "../../contexts";

const useDeleteCards = () => {
  const { cards, setCards } = useAppContext();

  const deleteCards = async (cardIds: number[]) => {
    try {
      const res = await fetch(`${API_URL}/cards`, {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cardIds),
      });

      if (!res.ok) throw new Error(`Server error: ${res.status}`);

      setCards(cards.filter((card) => !cardIds.includes(card.id)));

      console.log("Delete successful");
    } catch (error) {
      console.error("Failed to delete cards", error);
    }
  };

  return { deleteCards };
};

export default useDeleteCards;
