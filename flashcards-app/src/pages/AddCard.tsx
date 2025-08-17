import { useEffect, useState } from "react";
import Header from "../components/header";
import useCreateCard from "../hooks/cards/useCreateCard";
import type { Deck } from "../types/deck";
import SearchableMultiSelect from "../components/searchableMultiSelect";
import { useAppContext } from "../contexts";
import { CaretLeft } from "phosphor-react";
import { useNavigate } from "react-router-dom";

const AddCard = () => {
  const [front, setFront] = useState("");
  const [back, setBack] = useState("");
  const [selectedDecks, setSelectedDecks] = useState<Deck[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [showToast, setShowToast] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const { decks } = useAppContext();
  const navigate = useNavigate();
  const { createCard } = useCreateCard();

  const resetForm = () => {
    setFront("");
    setBack("");
    setSelectedDecks([]);
    setError(null);
  };

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

      await createCard({ front, back, deckNames });

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
        <div className="fixed top-20 left-1/2 -translate-x-1/2 bg-green-200 border-2 border-black px-4 py-2 rounded shadow transition-opacity z-50">
          Card saved!
        </div>
      )}

      <Header />
      <div className="flex relative justify-center items-start pt-10">
        <form
          onSubmit={handleSubmit}
          className="bg-white p-6 rounded shadow-md w-full max-w-md space-y-4 border-2 border-black"
        >
          <div className="relative mb-6">
            <button
              id="decks-back-button"
              className="absolute left-0 top-1/2 -translate-y-1/2 cursor-pointer"
              onClick={() => navigate("/")}
            >
              <CaretLeft size={24} />
            </button>
            <h1 className="text-xl font-bold text-center">Add a New Card</h1>
          </div>

          <div className="pt-4">
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
            <label className="block mb-2 font-medium">Back</label>
            <textarea
              value={back}
              onChange={(e) => setBack(e.target.value)}
              className="w-full p-2 border-2 rounded-md resize-none"
              rows={4}
              placeholder="Enter back text..."
            />
          </div>

          <div>
            <label className="block font-medium">Add to decks</label>
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
              {isSaving ? "Saving..." : "Save"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddCard;
