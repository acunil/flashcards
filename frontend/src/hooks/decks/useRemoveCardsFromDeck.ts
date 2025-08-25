import { API_URL } from "../urls";
import { useAppContext } from "../../contexts";
import { useAuthFetch } from "../../utils/authFetch";

const useRemoveCardsFromDeck = () => {
  const { cards, setCards } = useAppContext();
  const { authFetch } = useAuthFetch();

  const removeCards = async (cardIds: number[], deckId: number) => {
    try {
      const result = await authFetch(
        `${API_URL}/decks/${deckId}/remove-cards`,
        {
          method: "PATCH",
          body: JSON.stringify(cardIds),
        }
      );

      if (result === undefined) {
        // User was likely redirected to login
        return;
      }

      setCards(cards.filter((card) => !cardIds.includes(card.id)));
      console.log("Remove successful");
    } catch (error) {
      console.error("Failed to remove cards", error);
    }
  };

  return { removeCards };
};

export default useRemoveCardsFromDeck;
