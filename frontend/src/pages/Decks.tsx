import useCreateDeck from "../hooks/decks/useCreateDeck";
import DeckList from "../components/deck/deckList";
import Header from "../components/header";
import { useAppContext } from "../contexts";
import PageWrapper from "../components/pageWrapper";
import PageLoad from "../components/pageLoad";
import ContentWrapper from "../components/contentWrapper";
import BackButton from "../components/backButton";
import Heading from "../components/heading";

const Decks = () => {
  const { decks, loading, setDecks, selectedSubjectId, fetchDecks } =
    useAppContext();
  const { createDeck, loading: creating } = useCreateDeck(selectedSubjectId);

  const handleAddDeck = async (name: string) => {
    if (!name.trim()) return;

    const newDeck = await createDeck(name.trim());
    await fetchDecks();

    if (newDeck) {
      setDecks((prev) => [...prev, newDeck]);
    }
  };

  return (
    <PageWrapper className="bg-sky-200">
      <Header />
      <ContentWrapper>
        <div className="relative flex items-center mb-6">
          <div className="absolute left-0">
            <BackButton />
          </div>
          <Heading>Decks</Heading>
        </div>
        {(loading || creating) && <PageLoad />}
        {!loading && !creating && (
          <DeckList decks={decks} onAddDeck={handleAddDeck} />
        )}
      </ContentWrapper>
    </PageWrapper>
  );
};

export default Decks;
