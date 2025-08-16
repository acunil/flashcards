import type { CardResponse } from "./cardResponse";

export type Deck = {
  id: string;
  name: string;
  cardResponses: CardResponse[];
};
