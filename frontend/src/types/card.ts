import type { Deck } from "./deck";

export type Card = {
  id: number;
  front: string;
  back: string;
  hintFront: string;
  hintBack: string;
  decks: Deck[];
  avgRating: number;
  viewCount: number;
  lastViewed: string;
  lastRating: number;
  subjectId: number;
};
