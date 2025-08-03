import { useState } from "react";
import DifficultyButtons from "../components/difficultyButtons";
import FlipCard from "../components/filpCard";
import Header from "../components/header";
import useCards from "../hooks/useCards";

const Revise = () => {
    const { cards, loading, error } = useCards();
    const [currentIndex, setCurrentIndex] = useState(0);

    const handleNext = () => {
        setCurrentIndex((prevIndex) =>
            prevIndex < cards.length - 1 ? prevIndex + 1 : 0
        );
    };

    return (
        <div className="min-h-screen bg-pink-200">
            <Header />
            <main className="flex flex-col items-center gap-6 p-6">
                {loading && <p>Loading cards...</p>}
                {error && <p className="text-red-600">{error}</p>}
                {!loading && !error && cards.length > 0 && (
                    <>
                        <FlipCard card={cards[currentIndex]} />
                        <button
                            onClick={handleNext}
                            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
                        >
                            Next Card
                        </button>
                        <DifficultyButtons />
                    </>
                )}
                {!loading && !error && cards.length === 0 && <p>No cards available.</p>}
            </main>
        </div>
    );
};

export default Revise;
