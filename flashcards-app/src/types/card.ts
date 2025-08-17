import type { Deck } from "./deck";

export type Card = {
  id: number;
  front: string;
  back: string;
  decks: Deck[];
  avgRating: number;
  viewCount: number;
  lastViewed: string;
  lastRating: number;
};
