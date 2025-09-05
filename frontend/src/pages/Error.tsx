import Header from "../components/header";
import PageWrapper from "../components/pageWrapper";
import ContentWrapper from "../components/contentWrapper";

import Heading from "../components/heading";

const ErrorPage = () => {
  return (
    <PageWrapper className="bg-green-200 min-h-screen">
      <Header isErrorMode={true} />
      <ContentWrapper>
        <div className="relative flex items-center mb-6">
          <Heading>Something's gone wrong</Heading>
        </div>
        <div className="text-center">
          <p>Sorry, an error has occurred.</p>
          <p>Please contact support or try again later.</p>
        </div>
      </ContentWrapper>
    </PageWrapper>
  );
};

export default ErrorPage;
