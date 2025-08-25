import Header from "../components/header";
import PageWrapper from "../components/pageWrapper";
import BackButton from "../components/backButton";
import ContentWrapper from "../components/contentWrapper";
import Heading from "../components/heading";

const UserStats = () => {
  return (
    <PageWrapper className="bg-sky-200">
      <Header />
      <ContentWrapper>
        <div className="relative flex items-center h-12 mb-4">
          <BackButton />
          <Heading>User Stats</Heading>
        </div>
      </ContentWrapper>
    </PageWrapper>
  );
};

export default UserStats;
