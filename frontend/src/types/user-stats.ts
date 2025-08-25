import type { Card } from "./card";

export type UserStats = {
  totalCards: number;
  hardestCard: Card;
  mostViewedCard: Card;
  totalCardViews: number;
  totalLastRating1: number;
  totalLastRating2: number;
  totalLastRating3: number;
  totalLastRating4: number;
  totalLastRating5: number;
  totalUnviewedCards: number;
};
