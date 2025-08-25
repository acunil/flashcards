import { useEffect, useState } from "react";
import Header from "../components/header";
import useCreateCard from "../hooks/cards/useCreateCard";
import type { Deck } from "../types/deck";
import SearchableMultiSelect from "../components/searchableMultiSelect";
import { useAppContext } from "../contexts";
import { CaretLeft } from "phosphor-react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import useUpdateCard from "../hooks/cards/useUpdateCard";
import type { Card } from "../types/card";

const AddCard = () => {
  const [front, setFront] = useState("");
  const [back, setBack] = useState("");
  const [frontHint, setFrontHint] = useState("");
  const [backHint, setBackHint] = useState("");
  const [selectedDecks, setSelectedDecks] = useState<Deck[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const { decks, cards, selectedSubject, fetchDecks, refetchCards } =
    useAppContext();
  const navigate = useNavigate();
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
      setCardToEdit(cards.find((c) => c.id === Number(cardId)));
      if (cardToEdit) {
        setFront(cardToEdit.front);
        setBack(cardToEdit.back);
        setFrontHint(cardToEdit.hintFront);
        setBackHint(cardToEdit.hintBack);
        setSelectedDecks(cardToEdit.decks);
      }
    } else if (deckId) {
      const matchedDeck = decks.find((d) => d.id === Number(deckId));
      if (matchedDeck) {
        setSelectedDecks([matchedDeck]);
      }
    }
  }, [cardId, cards, cardToEdit, deckId, decks]);

  useEffect(() => {
    if (showToast) {
      const timer = setTimeout(() => setShowToast(false), 2000);
      return () => clearTimeout(timer);
    }
  }, [showToast]);

  const handleSubmit = async (e?: React.FormEvent) => {
    e?.preventDefault();

    if (!front.trim() || !back.trim()) {
      setError("Both front and back are required.");
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
        }); // new update hook
      } else {
        await createCard({
          front,
          back,
          hintFront: frontHint,
          hintBack: backHint,
          deckNames,
          subjectId: selectedSubject?.id || 0,
        });
      }

      await fetchDecks();
      await refetchCards();

      setShowToast(true);
    } catch {
      // handled inside hooks
    } finally {
      if (!isEditing) {
        resetForm();
      }
      setIsSaving(false);
    }
  };

  return (
    <div className="bg-yellow-200 min-h-screen">
      {showToast && (
        <div className="fixed top-20 left-1/2 -translate-x-1/2 bg-green-200 border-2 border-black px-4 py-2 rounded shadow transition-opacity z-50">
          Card saved!
        </div>
      )}

      <Header />
      <div className="flex relative justify-center items-start m-4 px-2">
        <form
          onSubmit={handleSubmit}
          className="bg-white p-6 rounded shadow-md w-full max-w-md space-y-4 border-2 border-black"
        >
          <div className="relative mb-6">
            <button
              id="decks-back-button"
              className="absolute left-0 top-1/2 -translate-y-1/2 cursor-pointer"
              onClick={() => navigate(-1)}
            >
              <CaretLeft size={24} />
            </button>
            <h1 className="text-xl font-bold text-center">
              {isEditing ? "Edit Card" : "Add a New Card"}
            </h1>
          </div>

          <div className="pt-4 pb-4 space-y-4">
            {/* Front Content */}
            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {selectedSubject?.frontLabel || "Front"}
              </label>
              <textarea
                value={front}
                onChange={(e) => setFront(e.target.value)}
                className="w-full p-3 border-2 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-sky-300 border-black"
                rows={4}
                placeholder="Enter front text..."
              />
            </div>

            {/* Front Hint */}
            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {`${selectedSubject?.frontLabel ?? "Front"} hint`}
              </label>
              <textarea
                value={frontHint || ""}
                onChange={(e) => setFrontHint(e.target.value)}
                className="w-full p-2 border rounded-lg resize-none bg-gray-50 border-gray-300 text-gray-600 focus:outline-none focus:ring-2 focus:ring-sky-200"
                rows={3}
                placeholder="Optional hint..."
              />
            </div>
          </div>

          <div className="pt-4 pb-4 space-y-4">
            {/* Back Content */}
            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {selectedSubject?.backLabel || "Back"}
              </label>
              <textarea
                value={back}
                onChange={(e) => setBack(e.target.value)}
                className="w-full p-3 border-2 rounded-lg resize-none focus:outline-none focus:ring-2 focus:ring-sky-300 border-black"
                rows={4}
                placeholder="Enter back text..."
              />
            </div>

            {/* Back Hint */}
            <div className="flex flex-col space-y-1">
              <label className="block font-medium text-gray-700">
                {`${selectedSubject?.backLabel ?? "Back"} hint`}
              </label>
              <textarea
                value={backHint || ""}
                onChange={(e) => setBackHint(e.target.value)}
                className="w-full p-2 border rounded-lg resize-none bg-gray-50 border-gray-300 text-gray-600 focus:outline-none focus:ring-2 focus:ring-sky-200"
                rows={3}
                placeholder="Optional hint..."
              />
            </div>
          </div>

          <div>
            <label className="block font-medium">Decks</label>
            <SearchableMultiSelect
              options={decks}
              selected={selectedDecks}
              onChange={setSelectedDecks}
            />
          </div>

          {error && <p className="text-red-400 text-sm">{error}</p>}

          <div className="flex justify-end">
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
      </div>
    </div>
  );
};

export default AddCard;
