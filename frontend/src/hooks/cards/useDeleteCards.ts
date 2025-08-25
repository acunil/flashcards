import { API_URL } from "../urls";
import { useAppContext } from "../../contexts";
import { useAuthFetch } from "../../utils/authFetch";

const useDeleteCards = () => {
  const { cards, setCards } = useAppContext();
  const { authFetch } = useAuthFetch();

  const deleteCards = async (cardIds: number[]) => {
    try {
      const result = await authFetch(`${API_URL}/cards`, {
        method: "DELETE",
        body: JSON.stringify(cardIds),
      });

      if (result === undefined) {
        // User was likely redirected to login
        return;
      }

      setCards(cards.filter((card) => !cardIds.includes(card.id)));
      console.log("Delete successful");
    } catch (error) {
      console.error("Failed to delete cards", error);
    }
  };

  return { deleteCards };
};

export default useDeleteCards;
