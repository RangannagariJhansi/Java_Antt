package wizard.cards;

import java.util.List;

public abstract class Cards {
	
	public static String toString(final Card[] cards) {
		if (cards == null) {
			return "[ ]";
		}
		
		String str = "[ ";
		for (int i = 0; i < cards.length; i++) {
			str += cards[i];
			
			if (i < cards.length - 1) {
				str += ", ";
			}
		}
		str += " ]";
		
		return str;
	}
	
	public static String toString(final List<Card> cards) {
		return toString(cards.toArray(new Card[cards.size()]));
		/**String str = "[ ";
		for (int i = 0; i < cards.size(); i++) {
			str += cards.get(i);
			
			if (i < cards.size() - 1) {
				str += ", ";
			}
		}
		str += " ]";
		
		return str;**/
	}
	
	public static String toStrings(final Card[] cards) {
		return null;
		// TODO:
	}

}
