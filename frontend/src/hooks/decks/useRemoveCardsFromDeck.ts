import { API_URL } from "../urls";
import { useAppContext } from "../../contexts";

const useRemoveCardsFromDeck = () => {
  const { cards, setCards } = useAppContext();

  const removeCards = async (cardIds: number[], deckId: number) => {
    try {
      const res = await fetch(`${API_URL}/decks/${deckId}/remove-cards`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cardIds),
      });

      if (!res.ok) throw new Error(`Server error: ${res.status}`);

      setCards(cards.filter((card) => !cardIds.includes(card.id)));

      console.log("Remove successful");
    } catch (error) {
      console.error("Failed to remove cards", error);
    }
  };

  return { removeCards };
};

export default useRemoveCardsFromDeck;
