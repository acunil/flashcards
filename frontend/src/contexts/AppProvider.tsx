import { useEffect, useState, useMemo } from "react";
import { AppContext } from "./AppContext";
import { useAllDecks } from "../hooks/decks";
import type { Deck } from "../types/deck";
import useCards from "../hooks/cards/useCards";
import useAllSubjects from "../hooks/subjects/useAllSubjects";

export const AppProvider = ({ children }: { children: React.ReactNode }) => {
  const { subjects: subjectsFromHook } = useAllSubjects();
  const { decks: allDecksFromHook, fetchDecks } = useAllDecks();

  // Local state
  const [allDecks, setAllDecks] = useState<Deck[]>(allDecksFromHook);
  const [allSubjects, setAllSubjects] = useState(subjectsFromHook);
  const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(
    null
  );

  // Fetch cards for the selected subject
  const { cards, setCards, loading, error, refetch } =
    useCards(selectedSubjectId);

  // Ensure first subject is selected by default
  useEffect(() => {
    if (allSubjects.length > 0 && selectedSubjectId === null) {
      setSelectedSubjectId(allSubjects[0].id);
    }
  }, [allSubjects, selectedSubjectId]);

  // Sync decks and subjects with hooks
  useEffect(() => setAllDecks(allDecksFromHook), [allDecksFromHook]);
  useEffect(() => setAllSubjects(subjectsFromHook), [subjectsFromHook]);

  // Refetch cards whenever selectedSubjectId changes
  useEffect(() => {
    if (selectedSubjectId !== null) {
      refetch(selectedSubjectId);
    } else {
      setCards([]); // no subject selected
    }
  }, [selectedSubjectId, refetch, setCards]);

  // Filter decks by selectedSubjectId
  const filteredDecks = useMemo<Deck[]>(() => {
    if (!selectedSubjectId) return [];
    return allDecks.filter((deck) => deck.subjectId === selectedSubjectId);
  }, [allDecks, selectedSubjectId]);

  // Currently selected subject object
  const selectedSubject = useMemo(
    () => allSubjects.find((s) => s.id === selectedSubjectId) || null,
    [allSubjects, selectedSubjectId]
  );

  // Wrappers to allow updates
  const setDecks = (updater: Deck[] | ((prev: Deck[]) => Deck[])) => {
    setAllDecks((prev) =>
      typeof updater === "function"
        ? (updater as (prev: Deck[]) => Deck[])(prev)
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

  return (
    <AppContext.Provider
      value={{
        decks: filteredDecks,
        cards,
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
