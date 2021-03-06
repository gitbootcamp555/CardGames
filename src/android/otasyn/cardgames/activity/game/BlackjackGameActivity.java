/**
 * Patrick John Haskins
 * Zachary Evans
 * CS7020 - Term Project
 */
package android.otasyn.cardgames.activity.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.otasyn.cardgames.R;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackBetTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackDoubleDownTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackHitTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackInsuranceTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackReadyTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackSplitTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackStandTask;
import android.otasyn.cardgames.communication.asynctask.games.blackjack.BlackjackSurrenderTask;
import android.otasyn.cardgames.communication.dto.GameAction;
import android.otasyn.cardgames.communication.dto.GamePlayer;
import android.otasyn.cardgames.communication.dto.gamestate.BlackjackState;
import android.otasyn.cardgames.communication.dto.gamestate.blackjack.PlayerHand;
import android.otasyn.cardgames.communication.dto.gamestate.blackjack.PlayerHands;
import android.otasyn.cardgames.communication.dto.moves.BlackjackMovesAvailable;
import android.otasyn.cardgames.contants.BlackjackContants;
import android.otasyn.cardgames.entity.HandHighlight;
import android.otasyn.cardgames.entity.PositionBox;
import android.otasyn.cardgames.scene.CardGameScene;
import android.otasyn.cardgames.sprite.CardSprite;
import android.otasyn.cardgames.utility.TextureUtility;
import android.otasyn.cardgames.utility.enumeration.Card;
import android.otasyn.cardgames.utility.enumeration.Rank;
import android.view.LayoutInflater;
import android.widget.NumberPicker;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BlackjackGameActivity extends CardGameActivity {

    private final static float POSITION_BOX_WIDTH = 125;
    private final static float POSITION_BOX_HEIGHT = 200;

    private final static float DECK_PADDING = 15;

    private final static float CARD_SPACING = 12;
    private final static float BUTTON_SPACING = 6;

    private Sprite deckSprite;
    private float deckX;
    private float deckY;
    private float deckWidth;
    private float deckHeight;

    private Font deckSizeFont;
    private Font insuranceFont;
    private Font playerFont;
    private Font betFont;
    private Font statusFont;

    private Text deckSizeText;
    private Text[] insuranceTexts;
    private Text[] playerTexts;
    private HandHighlight handHighlight;
    private List<Text> betTexts;
    private Text statusText;

    private Color insuranceColor;
    private Color playerColor;
    private Color handHighlightColor;
    private Color betColor;
    private Color statusColor;

    private PositionBox[] positionBoxes;
    private ITextureRegion[] betButtonTextureRegions;
    private ITextureRegion[] doubleDownButtonTextureRegions;
    private ITextureRegion[] hitButtonTextureRegions;
    private ITextureRegion[] insuranceButtonTextureRegions;
    private ITextureRegion[] readyButtonTextureRegions;
    private ITextureRegion[] splitButtonTextureRegions;
    private ITextureRegion[] standButtonTextureRegions;
    private ITextureRegion[] surrenderButtonTextureRegions;

    private ButtonSprite betButton;
    private ButtonSprite doubleDownButton;
    private ButtonSprite hitButton;
    private ButtonSprite insuranceButton;
    private ButtonSprite readyButton;
    private ButtonSprite splitButton;
    private ButtonSprite standButton;
    private ButtonSprite surrenderButton;

    @Override
    protected void onBeforeCreateEngineOptions() {
        setScreenOrientation(ScreenOrientation.LANDSCAPE_FIXED);
        setAlsoUpdateOnCurrentUserTurn(true);
    }

    @Override
    protected void onCreateCardGameResources() {
        setCardTextureRegions(TextureUtility.loadCards92x128(this));

        betButtonTextureRegions = TextureUtility.loadBlackjackBetButton(this);
        doubleDownButtonTextureRegions = TextureUtility.loadBlackjackDoubleDownButton(this);
        hitButtonTextureRegions = TextureUtility.loadBlackjackHitButton(this);
        insuranceButtonTextureRegions = TextureUtility.loadBlackjackInsuranceButton(this);
        readyButtonTextureRegions = TextureUtility.loadBlackjackReadyButton(this);
        splitButtonTextureRegions = TextureUtility.loadBlackjackSplitButton(this);
        standButtonTextureRegions = TextureUtility.loadBlackjackStandButton(this);
        surrenderButtonTextureRegions = TextureUtility.loadBlackjackSurrenderButton(this);

        handHighlightColor = new Color(88f, 77f, 209f, 0.5f);
        insuranceColor = new Color(1, 1, 0, 1);
        playerColor = new Color(0, 0, 1, 1);
        betColor = new Color(0, 0, 1, 1);
        statusColor = new Color(0, 0, 1, 1);

        deckSizeFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
        deckSizeFont.load();

        insuranceFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, insuranceColor.getARGBPackedInt());
        insuranceFont.load();

        playerFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, playerColor.getARGBPackedInt());
        playerFont.load();

        betFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 24, betColor.getARGBPackedInt());
        betFont.load();

        statusFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 40, statusColor.getARGBPackedInt());
        statusFont.load();
    }

    @Override
    protected void onCreateCardGameScene(final CardGameScene scene) {
        setGameMenuButtonVisibility(false);
        createAndDrawPositionBoxes(scene);
        createAndAttachButtons(scene);

        deckX = (getCameraWidth() / 2f) - 100f;
        deckY = 25f;
        deckWidth = getRedBack().getWidth();
        deckHeight = getRedBack().getHeight();

        betTexts = new ArrayList<Text>();

        statusText = new Text(6, 6, statusFont, "", 200, new TextOptions(HorizontalAlign.LEFT),
                getVertexBufferObjectManager());
        getCardGameScene().attachChild(statusText);

        displayAll();
    }

    private void createAndDrawPositionBoxes(final CardGameScene scene) {
        float arcCenterX = getCameraWidth() / 2f;
        float arcCenterY = -1200f;
        float arcRadius = 1500f;
        double spacingAngle = .046d * Math.PI;
        double currentAngle = 0;

        positionBoxes = new PositionBox[getGame().getPlayers().size()];
        insuranceTexts = new Text[getGame().getPlayers().size()];
        playerTexts = new Text[getGame().getPlayers().size()];
        for (int n = 0; n < getGame().getPlayers().size(); n++) {
            if (n == 0) {
                currentAngle += spacingAngle * ((getGame().getPlayers().size() - 1) / 2d);
            } else {
                currentAngle -= spacingAngle;
            }

            float pbX = arcCenterX + (arcRadius * (float) Math.sin(currentAngle));
            float pbY = arcCenterY + (arcRadius * (float) Math.cos(currentAngle));

            positionBoxes[n] = createPositionBox(pbX, pbY, 0);
            scene.attachChild(positionBoxes[n]);

            insuranceTexts[n] = new Text(pbX, pbY + POSITION_BOX_HEIGHT + 10, insuranceFont,
                    "", 20, new TextOptions(HorizontalAlign.CENTER), getVertexBufferObjectManager());
            getCardGameScene().attachChild(insuranceTexts[n]);

            playerTexts[n] = new Text(pbX, pbY + POSITION_BOX_HEIGHT + 5 + insuranceFont.getLineHeight(), playerFont,
                    "", 100, new TextOptions(HorizontalAlign.CENTER), getVertexBufferObjectManager());
            getCardGameScene().attachChild(playerTexts[n]);

        }
    }

    private void createAndAttachButtons(final CardGameScene scene) {
        betButton = createButton(0, 0, betButtonTextureRegions, new BetClickListener(), scene);
        doubleDownButton = createButton(0, 0, doubleDownButtonTextureRegions, new DoubleDownClickListener(), scene);
        hitButton = createButton(0, 0, hitButtonTextureRegions, new HitClickListener(), scene);
        insuranceButton = createButton(0, 0, insuranceButtonTextureRegions, new InsuranceClickListener(), scene);
        readyButton = createButton(0, 0, readyButtonTextureRegions, new ReadyClickListener(), scene);
        splitButton = createButton(0, 0, splitButtonTextureRegions, new SplitClickListener(), scene);
        standButton = createButton(0, 0, standButtonTextureRegions, new StandClickListener(), scene);
        surrenderButton = createButton(0, 0, surrenderButtonTextureRegions, new SurrenderClickListener(), scene);

        readyButton.setPosition(BUTTON_SPACING,
                getCameraHeight() - (2 * (BUTTON_SPACING + readyButton.getHeight())));
        betButton.setPosition(BUTTON_SPACING,
                getCameraHeight() - (BUTTON_SPACING + betButton.getHeight()));

        hitButton.setPosition((getCameraWidth() / 2f) - ((2f * hitButton.getWidth()) + (1.5f * BUTTON_SPACING)),
                getCameraHeight() - (BUTTON_SPACING + hitButton.getHeight()));
        standButton.setPosition((getCameraWidth() / 2f) - (standButton.getWidth() + (0.5f * BUTTON_SPACING)),
                getCameraHeight() - (BUTTON_SPACING + standButton.getHeight()));
        splitButton.setPosition((getCameraWidth() / 2f) + (0.5f * BUTTON_SPACING),
                getCameraHeight() - (BUTTON_SPACING + splitButton.getHeight()));
        doubleDownButton.setPosition((getCameraWidth() / 2f) + (doubleDownButton.getWidth() + (1.5f * BUTTON_SPACING)),
                getCameraHeight() - (BUTTON_SPACING + doubleDownButton.getHeight()));

        insuranceButton.setPosition(getCameraWidth() - (BUTTON_SPACING + insuranceButton.getWidth()),
                getCameraHeight() - (2 * (BUTTON_SPACING + insuranceButton.getHeight())));
        surrenderButton.setPosition(getCameraWidth() - (BUTTON_SPACING + surrenderButton.getWidth()),
                getCameraHeight() - (BUTTON_SPACING + surrenderButton.getHeight()));
    }

    private ButtonSprite createButton(final float x, final float y, final ITextureRegion[] textureRegions,
                                      final ButtonSprite.OnClickListener clickListener,
                                      final CardGameScene scene) {
        ButtonSprite buttonSprite = new ButtonSprite(
                x, y,
                textureRegions[TextureUtility.BUTTON_STATE_UP],
                textureRegions[TextureUtility.BUTTON_STATE_DOWN],
                textureRegions[TextureUtility.BUTTON_STATE_DISABLED],
                this.getVertexBufferObjectManager());
        buttonSprite.setOnClickListener(clickListener);
        scene.attachButtonSprite(buttonSprite);
        return buttonSprite;
    }

    private PositionBox createPositionBox(final float x, final float y, final float angle) {
        return new PositionBox(x, y, POSITION_BOX_WIDTH, POSITION_BOX_HEIGHT, angle, getVertexBufferObjectManager());
    }

    @Override
    protected void onLatestActionUpdated(final GameAction latestAction) {
        if (isGameLoaded() && isGameRunning()) {
            Debug.d("CardGames", "onLatestActionUpdated");
            displayAll();
        }
    }

    private BlackjackState getGameState() {
        return (BlackjackState) getLatestAction().getGameState();
    }

    private void displayAll() {
        clearCardSprites();
    }

    @Override
    protected void afterCardSpritesCleared() {
        clearEntities();
    }

    protected void clearEntities() {
        BlackjackGameActivity.this.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                if (handHighlight != null) {
                    handHighlight.detachSelf();
                    handHighlight.dispose();
                    handHighlight = null;
                }

                for (Text betText : betTexts) {
                    betText.detachSelf();
                    betText.dispose();
                }
                betTexts.clear();
                afterCleared();
            }
        });
    }

    protected void afterCleared() {
        if (getGameState() != null) {
            updateStatus();
            adjustButtonStates();
            displayDeck();
            displayDealerHand();
            displayPlayersHands();
        } else {
            Debug.d("CardGames", "GameState is null. [afterCleared()]");
        }
    }

    private void updateStatus() {
        BlackjackMovesAvailable movesAvailable = (BlackjackMovesAvailable) getLatestAction().getMovesAvailable();
        String status = "";
        if (movesAvailable != null) {
            if (movesAvailable.isReady()) {
                status = "Hand finished.\n";

                BlackjackState gameState = getGameState();
                GamePlayer player = getCurrentPlayer();
                PlayerHands playerHands = gameState.getPlayersHands().get(player);
                if (playerHands.isSurrendered()) {
                    status = "You surrendered.";
                } else {
                    if (isBlackjack(gameState.getDealerHand().getFaceUpCards())) {
                        if (playerHands.getHands().size() == 1) {
                            if (isBlackjack(playerHands.getHands().get(0).getHand())) {
                                status += "Push.";
                            } else {
                                status += "Dealer has Blackjack.";
                            }
                        }
                    } else {
                        if (playerHands.getHands().size() > 1) {
                            int numWin = 0;
                            int numPush = 0;
                            int numLose = 0;
                            for (PlayerHand playerHand : playerHands.getHands()) {
                                int handValue = evaluateHand(playerHand.getHand());
                                int dealerHandValue = evaluateHand(gameState.getDealerHand().getFaceUpCards());
                                if (handValue > BlackjackContants.BLACKJACK_MAX) {
                                    numLose++;
                                } else if (dealerHandValue > BlackjackContants.BLACKJACK_MAX) {
                                    numWin++;
                                } else if (handValue > dealerHandValue) {
                                    numWin++;
                                } else if (handValue == dealerHandValue) {
                                    numPush++;
                                } else {
                                    numLose++;
                                }
                            }
                            if (numWin > 0) {
                                status += "Win " + numWin + ".";
                            }
                            if (numPush > 0) {
                                status += (numWin > 0 ? " " : "") + "Push " + numPush + ".";
                            }
                            if (numLose > 0) {
                                status += (numWin > 0 || numPush > 0 ? " " : "") + "Lose " + numLose + ".";
                            }
                        } else {
                            List<Card> hand = playerHands.getHands().get(0).getHand();
                            if (isBlackjack(hand)) {
                                status += "You have Blackjack.";
                            } else {
                                int handValue = evaluateHand(hand);
                                int dealerHandValue = evaluateHand(gameState.getDealerHand().getFaceUpCards());
                                if (handValue > BlackjackContants.BLACKJACK_MAX) {
                                    status += "You busted.";
                                } else if (dealerHandValue > BlackjackContants.BLACKJACK_MAX) {
                                    status += "Dealer busts.";
                                } else if (handValue > dealerHandValue) {
                                    status += "You win.";
                                } else if (handValue == dealerHandValue) {
                                    status += "Push.";
                                } else {
                                    status += "You lose.";
                                }
                            }
                        }
                    }
                }

            } else if (movesAvailable.isBet()) {
                status = "Please make a bet.";
            } else if (movesAvailable.isInsurance()) {
                status = "Would you like insurance?";
            } else if (movesAvailable.isHit()
                    || movesAvailable.isStand()
                    || movesAvailable.isDoubleDown()
                    || movesAvailable.isSplit()
                    || movesAvailable.isSurrender()) {
                status = "Your turn.";
            } else {
                status = "Waiting on other players.";
            }
        }
        statusText.setText(status);
    }

    private boolean isBlackjack(final List<Card> hand) {
        if (hand.size() == 2
                && ((hand.get(0).getRank().getMaxBlackjackValue() == 10
                        && hand.get(1).getRank() == Rank.ACE)
                    || (hand.get(0).getRank() == Rank.ACE
                        && hand.get(1).getRank().getMaxBlackjackValue() == 10))) {
            return true;
        }
        return false;
    }

    private int evaluateHand(final List<Card> hand) {
        int numAces = 0;
        int total = 0;
        for (Card card : hand) {
            total += card.getRank().getMaxBlackjackValue();
            if (card.getRank() == Rank.ACE) {
                numAces++;
            }
        }
        while (numAces > 0 && total > BlackjackContants.BLACKJACK_MAX) {
            numAces--;
            total -= (Rank.ACE.getMaxBlackjackValue() - Rank.ACE.getMinBlackjackValue());
        }
        return total;
    }

    private void adjustButtonStates() {
        BlackjackMovesAvailable movesAvailable = (BlackjackMovesAvailable) getLatestAction().getMovesAvailable();
        if (movesAvailable != null) {
            betButton.setEnabled(movesAvailable.isBet());
            doubleDownButton.setEnabled(movesAvailable.isDoubleDown());
            hitButton.setEnabled(movesAvailable.isHit());
            insuranceButton.setEnabled(movesAvailable.isInsurance());
            readyButton.setEnabled(movesAvailable.isReady());
            splitButton.setEnabled(movesAvailable.isSplit());
            standButton.setEnabled(movesAvailable.isStand());
            surrenderButton.setEnabled(movesAvailable.isSurrender());
        }
    }

    private void displayDeck() {
        if (deckSprite == null) {
            if (getRedBack() != null) {
                Rectangle deckPositionBox = new Rectangle(deckX - DECK_PADDING, deckY - DECK_PADDING,
                                                          deckWidth + (2 * DECK_PADDING),
                                                          deckHeight + (2 * DECK_PADDING),
                                                          getVertexBufferObjectManager());
                deckPositionBox.setColor(Color.BLACK);
                getCardGameScene().attachChild(deckPositionBox);

                deckSprite = new Sprite(deckX, deckY, getRedBack(), this.getVertexBufferObjectManager());
                getCardGameScene().attachChild(deckSprite);
            }
        }
        try {
            Queue<Card> deck = getGameState().getDeck();
            if (deck.size() > 0) {
                Debug.d("CardGames", "Deck is visible.");
                deckSprite.setVisible(true);
            } else {
                Debug.d("CardGames", "Deck is not visible.");
                deckSprite.setVisible(false);
            }
            displayDeckSize(deck);
        } catch (NullPointerException e) {
            Debug.d("CardGames", e);
            displayDeckSize(null);
        }
    }

    private void displayDeckSize(final Queue<Card> deck) {
        if (deckSizeText == null) {
            deckSizeText = new Text(deckX, deckY + ((deckHeight - deckSizeFont.getLineHeight()) / 2), this.deckSizeFont,
                    "", 3, new TextOptions(HorizontalAlign.CENTER), getVertexBufferObjectManager());
            getCardGameScene().attachChild(deckSizeText);
        }
        deckSizeText.setText(deck != null ? String.valueOf(deck.size()) : "");
        deckSizeText.setPosition(deckX + ((deckWidth - deckSizeText.getWidth()) / 2), deckSizeText.getY());
    }

    private void displayPlayersHands() {
        if (getGameState() != null && getGameState().getPlayersHands().size() == getGame().getTurnOrder().size()) {
            for (int n = 0; n < getGame().getTurnOrder().size(); n++) {
                GamePlayer player = getGame().getTurnOrder().get(n);
                PlayerHands playerHands = getGameState().getPlayersHands().get(player);
                displayPlayerHands(n, player, playerHands);
            }
        }
    }

    private void displayPlayerHands(final int turnNumber, final GamePlayer player, final PlayerHands playerHands) {
        PositionBox box = positionBoxes[turnNumber];
        float boxCenterX = box.getX();
        float boxTopY = box.getY();
        float boxHeight = box.getHeight();

        float cardWidth = getRedBack().getWidth();
        float cardHeight = getRedBack().getHeight();

        float startY = boxTopY + ((boxHeight - cardHeight) / 2);
        for (int n = 0; n < playerHands.getHands().size(); n+=2) {
            boolean twoColumns = playerHands.getHands().size() > 1;
            GamePlayer nextActionPlayer = getLatestAction().getNextActionPlayer();
            if (twoColumns) {
                displayHand(playerHands.getHands().get(n), boxCenterX + (CARD_SPACING / 2), startY, CARD_SPACING,
                        (player.equals(nextActionPlayer) && playerHands.getHandTurn() == n));
                displayHand(playerHands.getHands().get(n+1), boxCenterX - (cardWidth + (CARD_SPACING / 2)), startY,
                        CARD_SPACING,
                        (player.equals(nextActionPlayer) && playerHands.getHandTurn() == (n+1)));
            } else {
                displayHand(playerHands.getHands().get(n),
                        boxCenterX - ((cardWidth + CARD_SPACING) / 2), startY, CARD_SPACING,
                        (player.equals(nextActionPlayer) && playerHands.getHandTurn() == n));
            }
            startY -= cardHeight + 20;
        }

        Text insuranceText = insuranceTexts[turnNumber];
        Integer insurance = playerHands.getInsurance();
        BlackjackMovesAvailable movesAvailable = getLatestAction().getMovesAvailable() != null
                ? (BlackjackMovesAvailable) getLatestAction().getMovesAvailable() : new BlackjackMovesAvailable();

        boolean dealerAceShowing = false;
        if (getGameState() != null
                && getGameState().getDealerHand() != null
                && !getGameState().getDealerHand().getFaceUpCards().isEmpty()) {
            if (getGameState().getDealerHand().getFaceDownCard() != null) {
                dealerAceShowing = getGameState().getDealerHand().getFaceUpCards().get(0).getRank() == Rank.ACE;
            } else {
                dealerAceShowing = getGameState().getDealerHand().getFaceUpCards().get(1).getRank() == Rank.ACE;
            }
        }

        if (dealerAceShowing && movesAvailable.isInsurance()) {
            insuranceText.setText("Insurance?");
        } else if (dealerAceShowing && insurance != null) {
            insuranceText.setText("Ins. ($" + insurance + ")");
        } else {
            insuranceText.setText("");
        }
        insuranceText.setPosition(boxCenterX - (insuranceText.getWidth() / 2), insuranceText.getY());

        Text playerText = playerTexts[turnNumber];
        playerText.setText(player.getFirstname() + "\n$" + getGameState().getPlayersBanks().get(player));
        playerText.setPosition(boxCenterX - (playerText.getWidth() / 2), playerText.getY());
    }

    private void displayHand(final PlayerHand playerHand, final float startX, final float startY, final float space,
                             final boolean isTurn) {
        List<CardSprite> handSprites = new ArrayList<CardSprite>(playerHand.getHand().size());
        for (int c = 0; c < playerHand.getHand().size(); c++) {
            Card card = playerHand.getHand().get(c);
            CardSprite cardSprite = getCardSprite(card);
            handSprites.add(cardSprite);

            cardSprite.setX(startX + (c * space));
            cardSprite.setY(startY + (c * space));
            cardSprite.showFace();
            cardSprite.setVisible(true);
            getCardGameScene().moveCardSpriteToFront(cardSprite);
        }

        if (isTurn) {
            handHighlight = new HandHighlight(handSprites, 6, handHighlightColor, this.getVertexBufferObjectManager());
            getCardGameScene().attachHandHighlight(handHighlight);
            for (CardSprite cardSprite : handSprites) {
                getCardGameScene().moveCardSpriteToFront(cardSprite);
            }
        }

        int totalBet = playerHand.getBet() + playerHand.getDoubleDown();
        Debug.d("CardGames", "Bet[" + startX + "," + startY + "]: $" + totalBet);
        Text betText = new Text(startX, startY, this.betFont,
                "$" + totalBet, 10, new TextOptions(HorizontalAlign.CENTER),
                getVertexBufferObjectManager());
        getCardGameScene().attachChild(betText);
        betText.setPosition(startX, startY - betFont.getLineHeight() - 4);
        betTexts.add(betText);
    }

    private void displayDealerHand() {
        float cardX = deckX + deckWidth + 20;
        float cardY = deckY;

        if (getGameState() != null && getGameState().getDealerHand() != null) {
            if (getGameState().getDealerHand().getFaceDownCard() != null) {
                CardSprite faceDownCardSprite = getCardSprite(getGameState().getDealerHand().getFaceDownCard());
                faceDownCardSprite.setX(cardX);
                faceDownCardSprite.setY(cardY);
                faceDownCardSprite.showBack();
                faceDownCardSprite.setVisible(true);
                getCardGameScene().moveCardSpriteToFront(faceDownCardSprite);
                cardX += CARD_SPACING;
            }
            for (Card card : getGameState().getDealerHand().getFaceUpCards()) {
                CardSprite faceUpCardSprite = getCardSprite(card);
                faceUpCardSprite.setX(cardX);
                faceUpCardSprite.setY(cardY);
                faceUpCardSprite.showFace();
                faceUpCardSprite.setVisible(true);
                getCardGameScene().moveCardSpriteToFront(faceUpCardSprite);
                cardX += CARD_SPACING;
            }
        }
    }

    private class BetClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Bet clicked.");
            BlackjackGameActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BlackjackGameActivity.this);
                    LayoutInflater inflater = BlackjackGameActivity.this.getLayoutInflater();
                    final AlertDialog dialog = alertBuilder
                            .setView(inflater.inflate(R.layout.popup_blackjack_bet, null))
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    AlertDialog alertDialog = (AlertDialog) dialog;
                                    int bet = ((NumberPicker) alertDialog.findViewById(R.id.betAmount)).getValue();
                                    alertDialog.dismiss();
                                    try {
                                        updateLatestAction((new BlackjackBetTask()).execute(
                                                getGame().getId().toString(), String.valueOf(bet)).get());
                                    } catch (Exception e) { }
                                }
                            })
                            .create();
                    dialog.show();
                    NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.betAmount);
                    numberPicker.setMinValue(BlackjackContants.MINIMUM_BET);
                    numberPicker.setMaxValue(getGameState().getPlayersBanks().get(getCurrentPlayer()));
                }
            });
        }
    }

    private class DoubleDownClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Double Down clicked.");
            BlackjackGameActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BlackjackGameActivity.this);
                    LayoutInflater inflater = BlackjackGameActivity.this.getLayoutInflater();
                    final AlertDialog dialog = alertBuilder
                            .setView(inflater.inflate(R.layout.popup_blackjack_doubledown, null))
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    AlertDialog alertDialog = (AlertDialog) dialog;
                                    int bet = ((NumberPicker) alertDialog.findViewById(
                                            R.id.doubleDownAmount)).getValue();
                                    alertDialog.dismiss();
                                    try {
                                        updateLatestAction((new BlackjackDoubleDownTask()).execute(
                                                getGame().getId().toString(), String.valueOf(bet)).get());
                                    } catch (Exception e) { }
                                }
                            })
                            .create();
                    dialog.show();

                    int bank = getGameState().getPlayersBanks().get(getCurrentPlayer());
                    int bet = 0;
                    PlayerHands playerHands = getGameState().getPlayersHands().get(getCurrentPlayer());
                    if (playerHands != null) {
                        int handTurn = playerHands.getHandTurn();
                        if (handTurn < playerHands.getHands().size()) {
                            bet = playerHands.getHands().get(handTurn).getBet();
                        }
                    }

                    NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.doubleDownAmount);
                    numberPicker.setMinValue(BlackjackContants.MINIMUM_DOUBLE_DOWN);
                    numberPicker.setMaxValue(Math.min(bank, bet));
                }
            });
        }
    }

    private class HitClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Hit clicked.");
            try {
                updateLatestAction((new BlackjackHitTask()).execute(getGame().getId().toString()).get());
            } catch (Exception e) { }
        }
    }

    private class InsuranceClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Insurance clicked.");
            BlackjackGameActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BlackjackGameActivity.this);
                    LayoutInflater inflater = BlackjackGameActivity.this.getLayoutInflater();
                    final AlertDialog dialog = alertBuilder
                            .setView(inflater.inflate(R.layout.popup_blackjack_insurance, null))
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final int which) {
                                    AlertDialog alertDialog = (AlertDialog) dialog;
                                    int bet = ((NumberPicker) alertDialog.findViewById(
                                            R.id.insuranceAmount)).getValue();
                                    alertDialog.dismiss();
                                    try {
                                        updateLatestAction((new BlackjackInsuranceTask()).execute(
                                                getGame().getId().toString(), String.valueOf(bet)).get());
                                    } catch (Exception e) { }
                                }
                            })
                            .create();
                    dialog.show();

                    int bank = getGameState().getPlayersBanks().get(getCurrentPlayer());
                    int bet = 0;
                    PlayerHands playerHands = getGameState().getPlayersHands().get(getCurrentPlayer());
                    if (playerHands != null) {
                        int handTurn = playerHands.getHandTurn();
                        if (handTurn < playerHands.getHands().size()) {
                            bet = playerHands.getHands().get(handTurn).getBet();
                        }
                    }

                    NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.insuranceAmount);
                    numberPicker.setMinValue(BlackjackContants.MINIMUM_INSURANCE);
                    numberPicker.setMaxValue(Math.min(bank, (int) Math.floor(bet / 2)));
                }
            });
        }
    }

    private class ReadyClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Ready clicked.");
            try {
                updateLatestAction((new BlackjackReadyTask()).execute(getGame().getId().toString()).get());
            } catch (Exception e) { }
        }
    }

    private class SplitClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Split clicked.");
            try {
                updateLatestAction((new BlackjackSplitTask()).execute(getGame().getId().toString()).get());
            } catch (Exception e) { }
        }
    }

    private class StandClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Stand clicked.");
            try {
                updateLatestAction((new BlackjackStandTask()).execute(getGame().getId().toString()).get());
            } catch (Exception e) { }
        }
    }

    private class SurrenderClickListener implements ButtonSprite.OnClickListener {
        @Override
        public void onClick(final ButtonSprite pButtonSprite,
                            final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            Debug.d("CardGames", "Surrender clicked.");
            try {
                updateLatestAction((new BlackjackSurrenderTask()).execute(getGame().getId().toString()).get());
            } catch (Exception e) { }
        }
    }
}
