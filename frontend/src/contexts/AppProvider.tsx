import { useEffect, useState, useMemo } from "react";
import { AppContext } from "./AppContext";
import { useAllDecks } from "../hooks/decks";
import type { Deck } from "../types/deck";
import useCards from "../hooks/cards/useCards";
import type { Card } from "../types/card";
import useSubjects from "../hooks/subjects/useSubjects";

export const AppProvider = ({ children }: { children: React.ReactNode }) => {
  const { decks, fetchDecks } = useAllDecks();
  const { cards, loading, error } = useCards();
  const { subjects: subjectsFromHook } = useSubjects();

  // Keep the *global* data for all subjects, decks, and cards
  const [allDecks, setAllDecks] = useState<Deck[]>(decks);
  const [allCards, setAllCards] = useState<Card[]>(cards);
  const [allSubjects, setAllSubjects] = useState(subjectsFromHook);

  const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(
    null
  );

  // ensure first subject is selected by default
  useEffect(() => {
    if (allSubjects.length > 0 && selectedSubjectId === null) {
      setSelectedSubjectId(allSubjects[0]?.id);
    }
  }, [allSubjects, selectedSubjectId]);

  // sync state with hook data
  useEffect(() => setAllDecks(decks), [decks]);
  useEffect(() => setAllCards(cards), [cards]);
  useEffect(() => setAllSubjects(subjectsFromHook), [subjectsFromHook]);

  // ✅ Filtered data based on selected subject
  const filteredDecks = useMemo<Deck[]>(() => {
    if (!selectedSubjectId) return [];
    return allDecks.filter((deck) => deck.subjectId === selectedSubjectId);
  }, [allDecks, selectedSubjectId]);

  const filteredCards = useMemo<Card[]>(() => {
    if (!selectedSubjectId) return [];
    return allCards.filter((card) => card.subjectId === selectedSubjectId);
  }, [allCards, selectedSubjectId]);

  // ✅ Wrappers so consumers can still set decks/cards/subjects
  const setDecks = (updater: Deck[] | ((prev: Deck[]) => Deck[])) => {
    setAllDecks((prev) =>
      typeof updater === "function"
        ? (updater as (prev: Deck[]) => Deck[])(prev)
        : updater
    );
  };

  const setCards = (updater: Card[] | ((prev: Card[]) => Card[])) => {
    setAllCards((prev) =>
      typeof updater === "function"
        ? (updater as (prev: Card[]) => Card[])(prev)
        : updater
    );
  };

  const setSubjects = (
    updater:
      | typeof allSubjects
      | ((prev: typeof allSubjects) => typeof allSubjects)
  ) => {
    setAllSubjects((prev) =>
      typeof updater === "function"
        ? (updater as (prev: typeof allSubjects) => typeof allSubjects)(prev)
        : updater
    );
  };

  // currently selected subject object
  const selectedSubject = useMemo(
    () => allSubjects.find((s) => s.id === selectedSubjectId) || null,
    [allSubjects, selectedSubjectId]
  );

  return (
    <AppContext.Provider
      value={{
        decks: filteredDecks,
        cards: filteredCards,
        loading,
        error,
        fetchDecks,
        setDecks,
        setCards,
        subjects: allSubjects,
        setSubjects,
        selectedSubjectId,
        setSelectedSubjectId,
        selectedSubject,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};
