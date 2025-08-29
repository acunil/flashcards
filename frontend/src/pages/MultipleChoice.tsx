import Header from "../components/header";
import PageWrapper from "../components/pageWrapper";
import BackButton from "../components/backButton";
import ContentWrapper from "../components/contentWrapper";
import Heading from "../components/heading";
import { useAppContext } from "../contexts";
import MultipleChoiceQuiz from "../components/multiChoiceQuiz";
import SelectDeckList from "../components/deck/selectDeckList";
import { useEffect, useState } from "react";
import type { Card } from "../types/card";
import { useNavigate } from "react-router-dom";

const MultipleChoice = () => {
  const { cards, decks, loading, error } = useAppContext();
  const navigate = useNavigate();
  const [selectedDeck, setSelectedDeck] = useState<number>();
  const [revisionCards, setRevisionCards] = useState<Card[]>([]);

  const getDeckCardCount = (deckId: number) =>
    cards.filter((card) => card.decks.some((deck) => deck.id === deckId))
      .length;

  const availableDecks = decks.filter((deck) => getDeckCardCount(deck.id) > 1);

  useEffect(() => {
    if (selectedDeck === undefined) return;

    if (selectedDeck === 0) {
      setRevisionCards(cards);
    } else {
      const deckCards = cards.filter((c) =>
        c.decks.some((d) => d.id === selectedDeck)
      );
      setRevisionCards(deckCards);
    }
  }, [selectedDeck, cards]);

  const handleSelectDeck = (id: number) => {
    setSelectedDeck(id);
  };

  const handleClickBack = () => {
    if (selectedDeck == undefined) {
      navigate(-1);
    }
    setSelectedDeck(undefined);
  };

  return (
    <PageWrapper className="bg-purple-200 min-h-screen">
      <Header />
      <ContentWrapper>
        {/* Header row */}
        <div className="flex items-center">
          <BackButton onClick={handleClickBack} />
          <Heading>Multiple Choice Mode</Heading>
        </div>
        {selectedDeck === undefined ? (
          <div className="flex flex-col items-center">
            <p className="mt-3 pb-1">
              Select a deck to revise in multiple choice mode
            </p>
            <SelectDeckList
              decks={availableDecks}
              onSelectDeck={handleSelectDeck}
            />
          </div>
        ) : (
          <>
            {!loading && !error && revisionCards.length === 0 && (
              <p>No cards available for this deck</p>
            )}

            {!loading && !error && revisionCards.length > 0 && (
              <MultipleChoiceQuiz cards={revisionCards} />
            )}
          </>
        )}
      </ContentWrapper>
    </PageWrapper>
  );
};

export default MultipleChoice;
