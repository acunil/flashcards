import DifficultyButtons from "../components/difficultyButtons";
import FlipCard from "../components/filpCard";
import Header from "../components/header";
import useCards from "../hooks/useCards";

const Revise = () => {
  const { cards, loading, error } = useCards();

  return (
    <div className="min-h-screen bg-pink-200">
      <Header />
      <main className="flex flex-col items-center gap-6 p-6">
        {loading && <p>Loading cards...</p>}
        {error && <p className="text-red-600">{error}</p>}
        {!loading && !error && cards.length > 0 && (
          <>
            <FlipCard cards={cards} />
            <DifficultyButtons />
          </>
        )}
        {!loading && !error && cards.length === 0 && <p>No cards available.</p>}
      </main>
    </div>
  );
};

export default Revise;
