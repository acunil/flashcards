import { useEffect, useState } from "react";
import Header from "../components/header";
import useCreateCard from "../hooks/cards/useCreateCard";
import useUpdateCard from "../hooks/cards/useUpdateCard";
import { useParams } from "react-router-dom";
import { useAppContext } from "../contexts";

const AddCard = () => {
  const { cardId } = useParams<{ cardId: string }>();
  const [front, setFront] = useState("");
  const [back, setBack] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  const editCardId = Number(cardId) || 0;
  const { cards } = useAppContext();
  const { createCard } = useCreateCard();
  const { updateCard } = useUpdateCard();

  const cardToEdit = cards.find((card) => card.id === editCardId) || null;

  const resetForm = () => {
    setFront("");
    setBack("");
    setError(null);
  };

  // Pre-fill form when editing
  useEffect(() => {
    if (cardToEdit) {
      setFront(cardToEdit.front);
      setBack(cardToEdit.back);
    } else {
      resetForm();
    }
  }, [cardToEdit]);

  // Auto-hide toast
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
      if (cardToEdit) {
        await updateCard(cardToEdit.id, {
          front,
          back,
          deckNamesDto: { deckNames: [] },
        });
      } else {
        await createCard({ front, back });
      }
      resetForm();
      setShowToast(true);
    } catch {
      // handled inside hooks
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="bg-yellow-200 min-h-screen">
      {showToast && (
        <div className="fixed top-9 left-1/2 -translate-x-1/2 bg-green-200 border-2 border-black px-4 py-2 rounded shadow transition-opacity">
          Card saved!
        </div>
      )}

      <Header />
      <div className="flex justify-center items-start pt-10">
        <form
          onSubmit={handleSubmit}
          className="bg-white p-6 rounded shadow-md w-full max-w-md space-y-4 border-2 border-black"
        >
          <h1 className="text-xl font-bold text-center">
            {cardToEdit ? "Edit Card" : "Add a New Card"}
          </h1>

          <div>
            <label className="block mb-1 font-medium">Front</label>
            <textarea
              value={front}
              onChange={(e) => setFront(e.target.value)}
              className="w-full p-2 border-2 rounded-md resize-none"
              rows={4}
              placeholder="Enter front text..."
            />
          </div>

          <div>
            <label className="block mb-1 font-medium">Back</label>
            <textarea
              value={back}
              onChange={(e) => setBack(e.target.value)}
              className="w-full p-2 border-2 rounded-md resize-none"
              rows={4}
              placeholder="Enter back text..."
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
              {isSaving ? "Saving..." : "Save"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddCard;
