import { useState, useEffect } from "react";
import Header from "../components/header";
import useCreateCard from "../hooks/cards/useCreateCard";
import useUpdateCard from "../hooks/cards/useUpdateCard";
import { useCardEdit } from "../contexts/CardEditContext";

const AddCard = () => {
  const { cardToEdit, setCardToEdit } = useCardEdit();

  const { updateCard } = useUpdateCard();

  const [front, setFront] = useState("");
  const [back, setBack] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);

  const { createCard } = useCreateCard();

  // On mount or cardToEdit change, pre-fill form if editing
  useEffect(() => {
    if (cardToEdit) {
      setFront(cardToEdit.textFront);
      setBack(cardToEdit.textBack);
    } else {
      setFront("");
      setBack("");
    }
  }, [cardToEdit]);

  const handleSubmit = async () => {
    if (!front.trim() || !back.trim()) {
      setError("Both front and back are required.");
      return;
    }

    setError(null);

    try {
      if (cardToEdit) {
        await updateCard(cardToEdit.id, {
          front,
          back,
          deckNamesDto: { deckNames: [] },
        });
        setCardToEdit(null);
      } else {
        await createCard({ front, back });
      }
      setFront("");
      setBack("");
      setShowToast(true);
      setTimeout(() => setShowToast(false), 2000);
    } catch {
      // error handled inside the hook
    }
  };

  return (
    <div className="bg-yellow-200">
      {/* Toast message */}
      {showToast && (
        <div className="fixed top-9 left-1/2 -translate-x-1/2 bg-green-200 border-2 border-black px-4 py-2 rounded shadow transition-opacity">
          Card saved!
        </div>
      )}

      <Header />
      <div className="min-h-screen bg-yellow-200 flex justify-center items-start pt-10">
        <div className="bg-white p-6 rounded shadow-md w-full max-w-md space-y-4 border-2 border-black">
          <h1 className="text-xl font-bold text-center">
            {cardToEdit ? "Edit card" : "Add a new card"}
          </h1>

          <div>
            <label className="block mb-1 font-medium">front</label>
            <textarea
              value={front}
              onChange={(e) => setFront(e.target.value)}
              className="w-full p-2 border-2 rounded-md resize-none"
              rows={4}
              placeholder="Enter front text..."
            />
          </div>

          <div>
            <label className="block mb-1 font-medium">back</label>
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
              onClick={handleSubmit}
              className="bg-green-300 px-4 py-2 rounded border-2 hover:bg-green-400 transition cursor-pointer"
            >
              save
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddCard;
