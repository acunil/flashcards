import { useEffect, useState, useMemo } from "react";
import { AppContext } from "./AppContext";
import useAllDecks from "../hooks/decks/useAllDecks";
import type { Deck } from "../types/deck";
import useAllCards from "../hooks/cards/useAllCards";
import useAllSubjects from "../hooks/subjects/useAllSubjects";

export const AppProvider = ({ children }: { children: React.ReactNode }) => {
  const {
    subjects: subjectsFromHook,
    error: errorSubjects,
    loading: loadingSubjects,
  } = useAllSubjects();

  // Keep local subjects but initialize from hook
  const [allSubjects, setAllSubjects] = useState<typeof subjectsFromHook>([]);
  useEffect(() => {
    setAllSubjects(subjectsFromHook);
  }, [subjectsFromHook]);

  const [selectedSubjectId, setSelectedSubjectId] = useState<number | null>(
    () => {
      const stored = localStorage.getItem("selectedSubjectId");
      return stored ? Number(stored) : null;
    }
  );

  // Fetch decks based on subjectId
  const {
    decks: allDecksFromHook,
    fetchDecks,
    loading: loadingDecks,
    error: errorDecks,
  } = useAllDecks(selectedSubjectId);

  const [allDecks, setAllDecks] = useState<Deck[]>([]);
  useEffect(() => {
    if (allDecksFromHook.length > 0) {
      setAllDecks(allDecksFromHook);
    } else {
      setAllDecks([]);
    }
  }, [allDecksFromHook]);

  // Fetch cards for the selected subject
  const {
    cards,
    setCards,
    loading: loadingCards,
    error: errorCards,
    refetch,
  } = useAllCards(selectedSubjectId);

  useEffect(() => {
    if (selectedSubjectId !== null) {
      localStorage.setItem("selectedSubjectId", String(selectedSubjectId));
    } else {
      localStorage.removeItem("selectedSubjectId");
    }
  }, [selectedSubjectId]);

  // Ensure first subject is selected by default
  useEffect(() => {
    if (subjectsFromHook.length > 0 && selectedSubjectId === null) {
      setSelectedSubjectId(subjectsFromHook[0].id);
    }
  }, [subjectsFromHook, selectedSubjectId]);

  // Refetch cards whenever selectedSubjectId changes
  useEffect(() => {
    if (selectedSubjectId !== null) {
      refetch(selectedSubjectId);
    } else {
      setCards([]); // no subject selected
    }
  }, [selectedSubjectId, refetch, setCards]);

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
        decks: allDecks,
        cards,
        loading: loadingSubjects || loadingDecks || loadingCards,
        error: errorSubjects || errorDecks || errorCards,
        fetchDecks: fetchDecks,
        refetchCards: refetch,
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
