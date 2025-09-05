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

      setCards(cards.filter((card) => !cardIds.includes(card.id)));

      if (result === undefined) {
        // User was likely redirected to login
        return;
      }
    } catch (error) {
      console.error("Failed to remove cards", error);
    }
  };

  return { removeCards };
};

export default useRemoveCardsFromDeck;
