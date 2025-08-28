import { useEffect, useState } from "react";
import Header from "../components/header";
import useCreateCard from "../hooks/cards/useCreateCard";
import type { Deck } from "../types/deck";
import SearchableMultiSelect from "../components/searchableMultiSelect";
import { useAppContext } from "../contexts";
import { useParams, useSearchParams } from "react-router-dom";
import useUpdateCard from "../hooks/cards/useUpdateCard";
import type { Card } from "../types/card";
import PageWrapper from "../components/pageWrapper";
import BackButton from "../components/backButton";
import Heading from "../components/heading";
import ContentWrapper from "../components/contentWrapper";
import Toast, { type ToastConfig } from "../components/toast";

const AddCard = () => {
  const [front, setFront] = useState("");
  const [back, setBack] = useState("");
  const [frontHint, setFrontHint] = useState("");
  const [backHint, setBackHint] = useState("");
  const [selectedDecks, setSelectedDecks] = useState<Deck[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [toast, setToast] = useState<ToastConfig | null>(null);

  const [isSaving, setIsSaving] = useState(false);

  const { decks, cards, selectedSubject, fetchDecks, refetchCards } =
    useAppContext();
  const { createCard } = useCreateCard();
  const { updateCard } = useUpdateCard();
  const { cardId } = useParams<{ cardId: string }>();
  const [isEditing, setIsEditing] = useState(false);
  const [cardToEdit, setCardToEdit] = useState<Card>();
  const [searchParams] = useSearchParams();
  const deckId = searchParams.get("deckId");

  const resetForm = () => {
    setFront("");
    setBack("");
    setFrontHint("");
    setBackHint("");
    setSelectedDecks([]);
    setError(null);
  };

  useEffect(() => {
    if (cardId) {
      setIsEditing(true);
      const foundCard = cards.find((c) => c.id === Number(cardId));
      setCardToEdit(foundCard);
      if (foundCard) {
        setFront(foundCard.front);
        setBack(foundCard.back);
        setFrontHint(foundCard.hintFront);
        setBackHint(foundCard.hintBack);
        setSelectedDecks(foundCard.decks);
      }
    } else if (deckId) {
      const matchedDeck = decks.find((d) => d.id === Number(deckId));
      if (matchedDeck) setSelectedDecks([matchedDeck]);
    }
  }, [cardId, cards, deckId, decks]);

  const handleSubmit = async (e?: React.FormEvent) => {
    e?.preventDefault();

    if (!front.trim() || !back.trim()) {
      setToast({
        message: "Both front and back are required.",
        isError: true,
        duration: 3000,
      });
      return;
    }

    setError(null);
    setIsSaving(true);

    try {
      const deckNames = selectedDecks.map((d) => d.name);

      if (isEditing && cardToEdit) {
        await updateCard({
          id: cardToEdit.id,
          front,
          back,
          hintFront: frontHint,
          hintBack: backHint,
          deckNames,
          subjectId: selectedSubject?.id || 0,
        });
      } else {
        const result = await createCard({
          front,
          back,
          hintFront: frontHint,
          hintBack: backHint,
          deckNames,
          subjectId: selectedSubject?.id || 0,
        });

        if (result.alreadyExisted == true) {
          setToast({
            message: "Card already exists!",
            isError: true,
            duration: 3000,
          });
          return;
        }
      }

      await fetchDecks();
      await refetchCards(selectedSubject?.id || 0);

      setToast({ message: "Card saved!", duration: 2000 });
      if (!isEditing) resetForm();
    } catch {
      setToast({
        message: "Could not add card.",
        isError: true,
        duration: 2000,
      });
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <PageWrapper className="bg-yellow-200">
      <Header />

      {toast && (
        <Toast
          message={toast.message}
          isError={toast.isError}
          confirm={toast.confirm}
          duration={toast.duration}
          onClose={() => setToast(null)}
        />
      )}

      <ContentWrapper>
        <form onSubmit={handleSubmit} className="p-1 sm:p-2">
          <div className="flex items-center mb-4 sm:mb-6 gap-2 sm:gap-4">
            <BackButton />
            <Heading>{isEditing ? "Edit Card" : "Add a New Card"}</Heading>
          </div>

          {/* Front Section */}
          <div className="space-y-2 sm:space-y-4">
            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {selectedSubject?.frontLabel || "Front"}
              </label>
              <textarea
                value={front}
                onChange={(e) => setFront(e.target.value)}
                className="w-full p-2 sm:p-3 border-2 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-sky-300 border-black"
                rows={3}
                placeholder="Enter front text..."
              />
            </div>

            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {`${selectedSubject?.frontLabel ?? "Front"} hint (optional)`}
              </label>
              <textarea
                value={frontHint || ""}
                onChange={(e) => setFrontHint(e.target.value)}
                className="w-full p-2 sm:p-3 border rounded-lg resize-none bg-white border-gray-300 text-gray-700 focus:outline-none focus:ring-2 focus:ring-sky-300"
                rows={2}
                placeholder="Optional hint..."
              />
            </div>
          </div>

          {/* Back Section */}
          <div className="mt-4 space-y-2 sm:space-y-4">
            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {selectedSubject?.backLabel || "Back"}
              </label>
              <textarea
                value={back}
                onChange={(e) => setBack(e.target.value)}
                className="w-full p-2 sm:p-3 border-2 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-sky-300 border-black"
                rows={3}
                placeholder="Enter back text..."
              />
            </div>

            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {`${selectedSubject?.backLabel ?? "Back"} hint (optional)`}
              </label>
              <textarea
                value={backHint || ""}
                onChange={(e) => setBackHint(e.target.value)}
                className="w-full p-2 sm:p-3 border rounded-lg resize-none bg-white border-gray-300 text-gray-700 focus:outline-none focus:ring-2 focus:ring-sky-300"
                rows={2}
                placeholder="Optional hint..."
              />
            </div>
          </div>

          {/* Deck Selector */}
          <div className="mt-4">
            <label className="block font-medium mb-1">Decks</label>
            <SearchableMultiSelect
              options={decks}
              selected={selectedDecks}
              onChange={setSelectedDecks}
            />
          </div>

          {/* Error */}
          {error && <p className="text-red-400 text-sm mt-2">{error}</p>}

          {/* Submit Button */}
          <div className="flex justify-end mt-4">
            <button
              type="submit"
              disabled={isSaving}
              className={`px-4 py-2 rounded border-2 transition cursor-pointer ${
                isSaving
                  ? "bg-gray-300 border-gray-400 cursor-not-allowed"
                  : "bg-green-300 border-black hover:bg-green-400"
              }`}
            >
              Save
            </button>
          </div>
        </form>
      </ContentWrapper>
    </PageWrapper>
  );
};

export default AddCard;
