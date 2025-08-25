import Header from "../components/header";
import PageWrapper from "../components/pageWrapper";
import BackButton from "../components/backButton";
import ContentWrapper from "../components/contentWrapper";
import Heading from "../components/heading";
import { useEffect } from "react";
import useUserStats from "../hooks/userStats/useUserStats";
import CardListItem from "../components/detailedCard/cardListItem";

const UserStats = () => {
  const userId = "11111111-1111-1111-1111-111111111111";
  const { userStats, getUserStats } = useUserStats(userId);

  useEffect(() => {
    getUserStats();
  }, [getUserStats]);

  useEffect(() => {
    if (userStats) console.log("User Stats:", userStats);
  }, [userStats]);

  return (
    <PageWrapper className="bg-sky-200 min-h-screen">
      <Header />
      <ContentWrapper>
        {/* Header row */}
        <div className="flex items-center mb-6">
          <BackButton />
          <Heading>User Stats</Heading>
        </div>

        {/* Stats container */}
        <div className="flex flex-col gap-4">
          <div className="flex justify-between px-2 rounded">
            <span className="font-bold">Total cards:</span>
            <span>{userStats?.totalCards ?? "-"}</span>
          </div>

          <div className="flex justify-between px-2 rounded">
            <span className="font-bold">Total card views:</span>
            <span>{userStats?.totalCardViews ?? "-"}</span>
          </div>

          <div className="flex justify-between px-2 rounded">
            <span className="font-bold">Total unviewed cards:</span>
            <span>{userStats?.totalUnviewedCards ?? "-"}</span>
          </div>

          <div className="flex flex-col justify-between px-2 rounded">
            <span className="font-bold">Ratings:</span>
            <div className="flex flex-row gap-2 my-2 justify-center">
              <div className="bg-red-300 border-2 p-2 w-12 text-center border-black rounded">
                {userStats?.totalLastRating1}
              </div>
              <div className="bg-orange-300 border-2 p-2 w-12 text-center border-black rounded">
                {userStats?.totalLastRating2}
              </div>
              <div className="bg-yellow-200 border-2 p-2 w-12 text-center border-black rounded">
                {userStats?.totalLastRating3}
              </div>
              <div className="bg-green-200 border-2 p-2 w-12 text-center border-black rounded">
                {userStats?.totalLastRating4}
              </div>
              <div className="bg-sky-200 border-2 p-2 w-12 text-center border-black rounded">
                {userStats?.totalLastRating5}
              </div>
            </div>
          </div>

          {userStats?.hardestCard && (
            <div className="flex flex-col justify-between p-2 rounded">
              <span className="font-bold">Hardest card:</span>
              <div className="py-2">
                <CardListItem
                  id={userStats?.hardestCard.id}
                  front={userStats?.hardestCard.front}
                  back={userStats?.hardestCard.back}
                  hintFront={userStats?.hardestCard.hintFront}
                  hintBack={userStats?.hardestCard.hintBack}
                  decks={userStats?.hardestCard.decks}
                  avgRating={userStats?.hardestCard.avgRating}
                  viewCount={userStats?.hardestCard.viewCount}
                  lastViewed={userStats?.hardestCard.lastViewed}
                  lastRating={userStats?.hardestCard.lastRating}
                  subjectId={userStats?.hardestCard.subjectId}
                />
              </div>
            </div>
          )}

          {userStats?.mostViewedCard && (
            <div className="flex flex-col justify-between p-2 rounded">
              <span className="font-bold">Most viewed card:</span>
              <div className="py-2">
                <CardListItem
                  id={userStats?.mostViewedCard.id}
                  front={userStats?.mostViewedCard.front}
                  back={userStats?.mostViewedCard.back}
                  hintFront={userStats?.mostViewedCard.hintFront}
                  hintBack={userStats?.mostViewedCard.hintBack}
                  decks={userStats?.mostViewedCard.decks}
                  avgRating={userStats?.mostViewedCard.avgRating}
                  viewCount={userStats?.mostViewedCard.viewCount}
                  lastViewed={userStats?.mostViewedCard.lastViewed}
                  lastRating={userStats?.mostViewedCard.lastRating}
                  subjectId={userStats?.mostViewedCard.subjectId}
                />
              </div>
            </div>
          )}
        </div>
      </ContentWrapper>
    </PageWrapper>
  );
};

export default UserStats;
