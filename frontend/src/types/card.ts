import type { Deck } from "./deck";
import type { Subject } from "./subject";

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
  subject: Subject;
};
