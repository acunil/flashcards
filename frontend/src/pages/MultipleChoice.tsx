import Header from "../components/header";
import PageWrapper from "../components/pageWrapper";
import BackButton from "../components/backButton";
import ContentWrapper from "../components/contentWrapper";
import Heading from "../components/heading";
import { useAppContext } from "../contexts";
import MultipleChoiceQuiz from "../components/multiChoiceQuiz";

const MultipleChoice = () => {
  const { cards, loading, error } = useAppContext();

  return (
    <PageWrapper className="bg-purple-200 min-h-screen">
      <Header />
      <ContentWrapper>
        {/* Header row */}
        <div className="flex items-center">
          <BackButton />
          <Heading>Multiple Choice Mode</Heading>
        </div>

        {/* Main content */}
        {loading && <p>Loading cards...</p>}
        {error && <p className="text-red-600">Error loading cards</p>}
        {!loading && !error && cards.length === 0 && (
          <p>No cards available for this subject</p>
        )}
        {!loading && !error && cards.length > 0 && (
          <MultipleChoiceQuiz cards={cards} />
        )}
      </ContentWrapper>
    </PageWrapper>
  );
};

export default MultipleChoice;
